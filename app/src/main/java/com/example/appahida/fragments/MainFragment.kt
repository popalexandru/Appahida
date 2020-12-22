package com.example.appahida.fragments

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appahida.R
import com.example.appahida.adapters.ExercicesListAdapter
import com.example.appahida.adapters.RepAdapter
import com.example.appahida.databinding.MainFragmentBinding
import com.example.appahida.db.dailyworkoutdb.ExerciseToDo
import com.example.appahida.db.workoutsdb.DaywithExercices
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.onVersionChanged
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Observer

@AndroidEntryPoint
class MainFragment : Fragment(), onVersionChanged, ExercicesListAdapter.FavClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }
    lateinit var workoutAdapter : ExercicesListAdapter
    private var _binding : MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val workoutsViewModel : WorkoutViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.getExercices(this)

        viewModel.reminderData.observe(viewLifecycleOwner){
            if(it == false){
                viewModel.setReminder()
                Timber.d("Setare reminder")
            }else{
                Timber.d("Reminder is already set")
            }
        }

        viewModel.waterQtyFlow.observe(viewLifecycleOwner){
            Timber.d("Setting max water to $it")
            if(it > 0){
                binding.progressWater.max = it
            }else{
                binding.progressWater.max = 2000
            }
        }

        workoutsViewModel.workoutByDay.observe(viewLifecycleOwner){
            Timber.d("Lista este $it")

            if(it == null){
                workoutsViewModel.insertDay()
            }else{
                val list = it.exercices
                workoutAdapter.submitList(list)
                makeListVisible()
            }
        }

        viewModel.waterLive.observe(viewLifecycleOwner){
            if(it.size > 0){

            val item = it.get(0)
            if(item.waterQty > 1000){
                binding.waterQty.setTextColor(Color.parseColor("#AAAA00"))
            }
            if(item.waterQty > 2000){
                binding.waterQty.setTextColor(Color.parseColor("#00AA00"))
            }

                val currentValue = Integer.parseInt(binding.waterQty.text.toString())
                val newValue = item.waterQty

                if((newValue - currentValue) > 900){
                    binding.waterQty.text = newValue.toString()
                    binding.progressWater.progress = newValue

                    Timber.d("ed ${binding.progressWater.max}")
                }else{
                    val valueAnimator = ValueAnimator.ofInt(currentValue, item.waterQty)
                    valueAnimator.setDuration(700)
                    valueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener{
                        binding.waterQty.text = it.getAnimatedValue().toString()
                        binding.progressWater.progress = it.getAnimatedValue() as Int
                    })

                    valueAnimator.start()
                }

                Timber.d("Set water to ${item.waterQty} ")
            }
        }

        setupRecyclerView()
        setWaterButtons()
        forSettingWater()

        binding.apply{
            adauga.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_addWorkoutFragment)}
            arrow.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_addWorkoutFragment) }

            deleteImg.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Esti sigur ca vrei sa stergi antrenamentul ?")
                    .setCancelable(true)
                    .setPositiveButton("Da"){ dialog, id ->
                        //workoutViewModel.deleteWorkout()
                        updateAdapter()
                    }
                    .setNegativeButton("Nu") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()


            }

            detailsButton.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                val customLayout = layoutInflater.inflate(R.layout.water_info, null)

                val exName : TextView = customLayout.findViewById(R.id.waterDescription)
                exName.text = "Cu toate ca apa pura nu contine calorii sau nutrienti organici, este un element vital pentru toate formele de viata pe care le cunoastem.\n" +
                        "\n" +
                        "Apa este un compus majoritar al corpului omenesc, fiind esentiala pentru mentinerea sanatatii celulelor si facilitand o multime de procese ale corpului, printre care transportul eficient al oxigenului catre celule, reglarea temperaturii corpului, lubrifierea articulatiilor sau mentinerea sanatatii gastrointestinale.\n" +
                        "\n" +
                        "Tocmai de aceea, cel mai bun dozator de apa este acela care iti ofera o apa pura, perfect filtrata, indiferent de momentul din zi sau de locul in care te afli."

                builder.setView(customLayout)
                        .setCancelable(true)

                val alertdialog = builder.create()
                alertdialog.show()
            }
        }

    }

    private fun forSettingWater(){
        binding.settingsButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.water_settings, null))
                .setCancelable(true)

            val alert = builder.create()
            alert.setContentView(R.layout.water_settings)
            alert.show()

            val picker : NumberPicker = alert.findViewById(R.id.picker)
            val pickerMl : NumberPicker = alert.findViewById(R.id.picker_2)

            val ml = arrayOf("ml")
            pickerMl.displayedValues = ml

            val values = arrayOf("1500", "2000", "2500", "3000", "3500", "4000", "4500", "5000", "5500", "6000", "6500", "7000", "7500", "8000", "8500", "9000")
            picker.wrapSelectorWheel = true
            picker.displayedValues = values
            picker.minValue = 0
            picker.maxValue = 15


            alert.setOnCancelListener {
                val value = Integer.parseInt(values[picker.value])
                if(value > 0){
                    viewModel.updateWaterMax(value)
                }
            }

        }
    }
    private fun setWaterButtons(){
        binding.size300.setOnClickListener {
            viewModel.addWater(300)
        }

        binding.size900.setOnClickListener {
            viewModel.addWater(900)
        }

        binding.size500.setOnClickListener {
            viewModel.addWater(500)
        }
    }
    private fun setupRecyclerView(){
        val context = requireContext()
        workoutAdapter = ExercicesListAdapter(this, context)

        binding.apply{
            workoutRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                hasFixedSize()
                adapter = workoutAdapter
                addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            }
        }


    }

    override fun onVersionChanged(version: Int) {
        viewModel.updateVersion(version)
    }

    override fun onFavListener(item: Exercice) {
        workoutsViewModel.deleteExercice(item)
    }

    override fun onAddClick(repsRecyclerView: RepAdapter) {
        TODO("Not yet implemented")
    }

    fun updateAdapter(){
        workoutAdapter.notifyDataSetChanged()
    }

    private fun makeListVisible(){
        binding.workoutRecyclerView.visibility = View.VISIBLE
        binding.gantera.visibility = View.GONE
        binding.mesaj.visibility = View.GONE
        binding.adauga.text = "Adauga exercitiu"
        binding.deleteImg.visibility = View.VISIBLE
    }

    private fun hideList(){
        binding.deleteImg.visibility = View.GONE
        binding.workoutRecyclerView.visibility = View.GONE
        binding.gantera.visibility = View.VISIBLE
        binding.mesaj.visibility = View.VISIBLE
        binding.adauga.text = "Adauga un antrenament"
    }

    var pressedBack = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            if(pressedBack + 2000 > System.currentTimeMillis()){
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(a)
            }else{
                pressedBack = System.currentTimeMillis()
                Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }

        callback.isEnabled = true
    }

}