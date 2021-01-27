package com.example.appahida.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.*
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import coil.load
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter
import com.example.appahida.R
import com.example.appahida.adapters.*
import com.example.appahida.constants.Constants
import com.example.appahida.constants.Constants.ACTION_PAUSE_SERVICE
import com.example.appahida.constants.Constants.ACTION_START_OR_RESUME
import com.example.appahida.constants.Constants.ACTION_START_PAUSE
import com.example.appahida.constants.Constants.ACTION_STOP_SERVICE
import com.example.appahida.constants.Constants.countValues
import com.example.appahida.constants.Constants.weightValues
import com.example.appahida.databinding.WorkoutDoerLayoutBinding
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import com.example.appahida.services.WorkingService
import com.example.appahida.utils.Utility
import com.example.appahida.utils.onQueryTextChanged
import com.example.appahida.viewmodels.WorkoutViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.unified_ad_view.view.*

@AndroidEntryPoint
class WorkoutDoerFragment : Fragment(), WorkoutEditorListAdapter.FavClickListener{
    private var _binding : WorkoutDoerLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutViewModel by activityViewModels()

    private var curTimeInMilis = 0L
    private var isWorking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

/*        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_workoutEditor_to_mainFragment)
        }
        callback.isEnabled = true*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = WorkoutDoerLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterHor = WorkoutEditorListAdapter(requireContext(), this)

        WorkingService.timeWorkedInMilliseconds.observe(viewLifecycleOwner){
            curTimeInMilis = it
            val formattedTime = Utility.getFormattedStopWatchTime(it)
            binding.seconds.text = formattedTime
        }

        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        WorkingService.timePause.observe(viewLifecycleOwner){
            binding.pauseTimer.text = it.toString()

            if(it < 3 && isWorking){
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))

                if(it == 0L){
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    binding.pauseTimer.visibility = View.GONE
                }
            }
        }

        WorkingService.isWorking.observe(viewLifecycleOwner){
            isWorking = it
            if(isWorking){
                binding.startTimer.setImageResource(R.drawable.ic_baseline_pause_24)
            }else{
                binding.startTimer.setImageResource(R.drawable.ic_start)
            }
        }

        viewModel.exercicesForToday.observe(viewLifecycleOwner){ dayWithExercise ->
            if(dayWithExercise != null){
                if(dayWithExercise.exercices.size > 0){
                    val exercicesList = dayWithExercise.exercices
                    adapterHor.submitList(exercicesList)
                }
            }else{
                findNavController().navigate(R.id.action_workoutEditor_to_mainFragment)
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.apply {

            horRecycler.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                hasFixedSize()
                adapter = adapterHor

                //loadNativeAds(adapterHor)
            }

            startTimer.setOnClickListener {
                if(isWorking){
                    sendCommandToService(ACTION_PAUSE_SERVICE)
                }else{
                    sendCommandToService(ACTION_START_OR_RESUME)
                }
                stopTimer.setImageResource(R.drawable.ic_stop)
            }

            stopTimer.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Vrei sa inchei antrenamentul ?")
                        .setCancelable(true)
                        .setPositiveButton("Da"){ dialog, id ->
                            WorkingService.timeWorkedInMilliseconds.value?.let { it1 ->
                                Toast.makeText(requireContext(), "Workout finished", Toast.LENGTH_SHORT).show()
                                viewModel.finishTodaysWorkout(
                                    it1
                                )
                            }
                            WorkingService.isWorking.postValue(false)
                            //findNavController().navigate(R.id.action_workoutEditor_to_mainFragment)
                            sendCommandToService(ACTION_STOP_SERVICE)
                            findNavController().navigateUp()
                        }
                        .setNegativeButton("Nu") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
            }
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.horRecycler)
    }

    private fun sendCommandToService(action : String) =
            Intent(requireContext(), WorkingService::class.java).also {
                it.action = action
                requireContext().startService(it)
            }

    override fun onFavListener(item: ExercicesWithReps) {
        TODO("Not yet implemented")
    }

    override fun onAddClick(repsRecyclerView: RepAdapter, exId : Int) {
        val builder = AlertDialog.Builder(context)
        val customLayout = layoutInflater.inflate(R.layout.choose_rep_layout, null)

        val inputRep = customLayout.findViewById(R.id.inputCount) as TextInputEditText
        val inputWei = customLayout.findViewById(R.id.inputWeight) as TextInputEditText

        val weightPicker = customLayout.findViewById(R.id.pickerWeight) as NumberPicker
        val countPicker = customLayout.findViewById(R.id.pickerCount) as NumberPicker

        val checkBox = customLayout.findViewById(R.id.pauza_check_box) as CheckBox

        weightPicker.displayedValues = weightValues
        countPicker.displayedValues = countValues

        weightPicker.wrapSelectorWheel
        countPicker.wrapSelectorWheel

        weightPicker.minValue = 0
        weightPicker.maxValue = weightValues.size - 1

        countPicker.minValue = 0
        countPicker.maxValue = countValues.size - 1

        if(viewModel.lastRepsAdded != 0 && viewModel.lastWeightAdded != 0){
            weightPicker.value = viewModel.lastWeightAdded
            countPicker.value = viewModel.lastRepsAdded

            inputRep.setText(countPicker.value.toString())
            inputWei.setText(weightPicker.value.toString())
            //inputWei.setText(countValues[countPicker.value])
            //inputWei.setText(weightValues[weightPicker.value])
        }else{
            inputWei.setText(weightValues[0])
            inputRep.setText(countValues[0])
        }



        weightPicker.setOnValueChangedListener { _, value, i2 ->
            inputWei.setText(weightValues[i2])
        }

        countPicker.setOnValueChangedListener { _, value, i2 ->
            inputRep.setText(countValues[i2])
        }

        builder.setView(customLayout)
                .setCancelable(true)
                .setPositiveButton("Ok"){ dialog, id ->

                    if(inputRep.text?.isNotEmpty() == true && inputWei.text?.isNotEmpty() == true){
                        val rep = Integer.parseInt(inputRep.text.toString())
                        val wei = Integer.parseInt(inputWei.text.toString())

                        viewModel.addReps(wei, rep, exId)

                        viewModel.lastWeightAdded = rep
                        viewModel.lastRepsAdded = wei

                        if(checkBox.isChecked){
                            sendCommandToService(ACTION_START_PAUSE)
                            binding.pauseTimer.visibility = View.VISIBLE
                        }

                    }else{
                        Toast.makeText(
                                requireContext(),
                                "Valori invalide",
                                Toast.LENGTH_SHORT
                        ).show()
                    }

                }

        val alertdialog = builder.create()
        alertdialog.show()
    }

    override fun onExchangeExercice(exercice : Exercice) {
        val parameter = Constants.EXERCICE_EXCHANGE

        val bundle = bundleOf("param" to parameter)
        bundle.putSerializable("exId", exercice)

        findNavController().navigate(R.id.action_workoutEditor_to_addWorkoutFragment, bundle)
    }

    private fun loadNativeAds(categoryAdapter: WorkoutEditorListAdapter) {
        /*val builder = AdLoader.Builder(requireContext(), resources.getString(R.string.native_ad_unit_id))

        val adLoader = builder.forUnifiedNativeAd(UnifiedNativeAd.OnUnifiedNativeAdLoadedListener {

            val adView = layoutInflater.inflate(R.layout.unified_ad_view, null) as UnifiedNativeAdView

            adView.ad_headline.text = it.headline
            adView.ad_app_icon.load(it.icon.uri)

            adView.setNativeAd(it)
            //adView.removeAllViews()

        }).withAdListener(object : AdListener(){
            override fun onAdFailedToLoad(p0: LoadAdError?) {
                super.onAdFailedToLoad(p0)

                Toast.makeText(requireContext(), "Failed ${p0?.message}", Toast.LENGTH_SHORT).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())*/


        val adapter = AdmobNativeAdAdapter.Builder.with(resources.getString(R.string.native_ad_unit_id),
            categoryAdapter,
            "custom").adItemInterval(3).build()


        binding.horRecycler.adapter = adapter
    }
    /*
       private lateinit var timer_menu : MenuItem

       override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
           super.onCreateOptionsMenu(menu, inflater)

           timer_menu = menu.findItem(R.menu.timer_menu)

           inflater.inflate(R.menu.timer_menu, menu)
       }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}