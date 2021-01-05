package com.example.appahida.fragments

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.appahida.R
import com.example.appahida.adapters.ExercicesListAdapter
import com.example.appahida.adapters.RepAdapter
import com.example.appahida.constants.Constants
import com.example.appahida.constants.Constants.ACTION_START_OR_RESUME
import com.example.appahida.databinding.MainFragmentBinding
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import com.example.appahida.db.workoutsdb.Reps
import com.example.appahida.objects.RepCount
import com.example.appahida.onVersionChanged
import com.example.appahida.services.WorkingService
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import com.example.appahida.workers.WaterWorker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.exercice_added_item.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainFragment : Fragment(), onVersionChanged, ExercicesListAdapter.FavClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }
    lateinit var workoutAdapter : ExercicesListAdapter
    lateinit var repsAdapter : RepAdapter

    private var _binding : MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val workoutsViewModel : WorkoutViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    lateinit var alert : AlertDialog
    lateinit var progressDialog : ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when(it){
                    is MainViewModel.UiState.Success -> {
                            //alert.dismiss()
                        progressDialog.dismiss()
                    }

                    is MainViewModel.UiState.Loading -> {
/*                        val builder = AlertDialog.Builder(context)

                        val customLayout = layoutInflater.inflate(R.layout.loading_layout, null)

                        builder.setView(customLayout)
                            .setCancelable(true)

                        alert = builder.create()
                        alert.show()
                        delay(400)*/

                        progressDialog = ProgressDialog.show(requireContext(), "", "Loading..", true)
                        progressDialog.show()
                        delay(500)
                    }

                    else -> Unit
                }
            }
        }


        setHorizontalCalendar()

        viewModel.getExercices(this)

        workoutsViewModel.todaysValue.observe(viewLifecycleOwner){
            if(it == null){
                Timber.d("Today entitiy not created, will create one..")

                workoutsViewModel.insertDay(0, true)
            }else{
                Timber.d("Today entitiy already existing for today")

                if(it.isWorkoutDone){
                    //getView()?.let { it1 -> Snackbar.make(it1, "Workout is done ! :) duration ${TimeUnit.MILLISECONDS.toSeconds(it.workoutDuration)}", Snackbar.LENGTH_SHORT).show() }
                    binding.startWorkout.text = "Done - ${TimeUnit.MILLISECONDS.toSeconds(it.workoutDuration)}"
                }else{
                    binding.startWorkout.text = "INCEPE ANTRENAMENT"
                }
            }
        }

        workoutsViewModel.exercicesForToday.observe(viewLifecycleOwner){ dayWithExercise ->
            Timber.d("Lista este $dayWithExercise")

            if(dayWithExercise != null){
                if(dayWithExercise.exercices.size > 0){
                    val exercicesList = dayWithExercise.exercices
                    workoutAdapter.submitList(exercicesList)
                    makeListVisible()
                }else{
                    hideList()
                }
            }else{
                hideList()
            }

        }

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

        viewModel.waterLive.observe(viewLifecycleOwner){
            if(it != null){

            //val item = it.get(0)
            val item = it
            if(it > 1000){
                binding.waterQty.setTextColor(Color.parseColor("#AAAA00"))
            }
            if(it > 2000){
                binding.waterQty.setTextColor(Color.parseColor("#00AA00"))
            }

                val currentValue = Integer.parseInt(binding.waterQty.text.toString())
                val newValue = it

                if((newValue - currentValue) > 900){
                    binding.waterQty.text = newValue.toString()
                    binding.progressWater.progress = newValue

                    Timber.d("ed ${binding.progressWater.max}")
                }else{
                    val valueAnimator = ValueAnimator.ofInt(currentValue, it)
                    valueAnimator.setDuration(700)
                    valueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener{
                        binding.waterQty.text = it.getAnimatedValue().toString()
                        binding.progressWater.progress = it.getAnimatedValue() as Int
                    })

                    valueAnimator.start()
                }

                Timber.d("Set water to ${it} ")
            }else{
                binding.waterQty.text = "0"
                binding.progressWater.progress = 0
            }
        }

        setupRecyclerView()
        setWaterButtons()
        forSettingWater()

        binding.apply{
            adauga.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_addWorkoutFragment)}
            arrow.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_addWorkoutFragment) }
            //editBtn.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_workoutEditor) }

            startWorkout.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Vrei sa incepi antrenamentul ?")
                        .setCancelable(true)
                        .setPositiveButton("Da"){ dialog, id ->
                            Toast.makeText(requireContext(), "Antrenamentul incepe..", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_mainFragment_to_workoutEditor)
                            sendCommandToService(ACTION_START_OR_RESUME)
                        }
                        .setNegativeButton("Nu") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
            }

            deleteImg.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Esti sigur ca vrei sa stergi antrenamentul ?")
                    .setCancelable(true)
                    .setPositiveButton("Da"){ dialog, id ->
                        workoutsViewModel.deleteTodayExercices()
                        //updateAdapter()
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

    private fun setHorizontalCalendar(){
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)

        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)

        val calendar = HorizontalCalendar.Builder(requireActivity(), R.id.calendarview)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure().showTopText(false)
                .end()
                .build()

        val listener = object : HorizontalCalendarListener(){
            override fun onDateSelected(date: Calendar?, position: Int) {
                viewModel.postState(MainViewModel.UiState.Loading)
                Timber.d("Date selected ${date?.timeInMillis}")

                val dateSelectedStart = Calendar.getInstance()
                dateSelectedStart.timeInMillis = date?.timeInMillis!!


                dateSelectedStart.set(Calendar.HOUR_OF_DAY, 0)
                dateSelectedStart.set(Calendar.MINUTE, 0)
                dateSelectedStart.set(Calendar.SECOND, 1)
                dateSelectedStart.set(Calendar.MILLISECOND, 0)

                val time = dateSelectedStart.timeInMillis

                viewModel.selectedDate.value = time
                workoutsViewModel.selectedDate.value = time

                if(DateUtils.isToday(time)){
                    /* its fine */
                        binding.apply {
                            adauga.isVisible = true
                            arrow.isVisible = true
                            editBtn.isVisible = true
                            mesaj.text = "Nu ai un antrenament inca"
                        }
                }else{
                    binding.apply {
                        adauga.isVisible = false
                        arrow.isVisible = false
                        editBtn.isVisible = false
                        mesaj.text = "Nu te-ai antrenat in aceasta zi"
                    }
                }


                viewModel.postState(MainViewModel.UiState.Success)
            }
        }

        calendar.calendarListener = listener
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

    override fun onFavListener(item: ExercicesWithReps) {
        //val ex = Exercice(item.ex)
        workoutsViewModel.deleteExercice(item.exercice)
        //Toast.makeText(requireContext(), "trebuie impl", Toast.LENGTH_SHORT).show()
    }

    override fun onAddClick(repsRecyclerView: RepAdapter, exId : Int) {
        repsAdapter = repsRecyclerView
        val builder = AlertDialog.Builder(context)
        val customLayout = layoutInflater.inflate(R.layout.choose_rep_layout, null)

        val inputRep = customLayout.findViewById(R.id.inputCount) as TextInputEditText
        val inputWei = customLayout.findViewById(R.id.input_wei) as TextInputEditText

        builder.setView(customLayout)
                .setCancelable(true)
                .setPositiveButton("Ok"){ dialog, id ->

                    val rep = Integer.parseInt(inputRep.text.toString())
                    val wei = Integer.parseInt(inputWei.text.toString())

                    workoutsViewModel.addReps(rep, wei, exId)
/*
                    repsRecyclerView.submitList(viewModel.list)
                    repsRecyclerView.notifyDataSetChanged()*/
                }

        val alertdialog = builder.create()
        alertdialog.show()
    }

    fun updateAdapter(){
        workoutAdapter.notifyDataSetChanged()
    }

    private fun makeListVisible(){
        binding.workoutRecyclerView.visibility = View.VISIBLE
        binding.gantera.visibility = View.GONE
        binding.mesaj.visibility = View.GONE
        binding.editBtn.visibility = View.VISIBLE
        binding.adauga.text = "Adauga exercitiu"
        binding.deleteImg.visibility = View.VISIBLE
        binding.startWorkout.visibility = View.VISIBLE
    }

    private fun hideList(){
        binding.startWorkout.visibility = View.GONE
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

    private fun sendCommandToService(action : String) =
            Intent(requireContext(), WorkingService::class.java).also {
                it.action = action
                requireContext().startService(it)
            }

/*    override fun onAddClick(adapter: RepAdapter) {

        val builder = AlertDialog.Builder(context)
        val customLayout = layoutInflater.inflate(R.layout.choose_rep_layout, null)

        val inputRep = customLayout.findViewById(R.id.inputCount) as TextInputEditText
        val inputWei = customLayout.findViewById(R.id.input_wei) as TextInputEditText

        builder.setView(customLayout)
                .setCancelable(true)
                .setPositiveButton("Ok"){ dialog, id ->


                    val rep = inputRep.text.toString()
                    val wei = inputWei.text.toString()

                    viewModel.list.add(
                            RepCount(Integer.parseInt(rep), Integer.parseInt(wei)
                            )
                    )

                    adapter.submitList(viewModel.list)
                    adapter.notifyDataSetChanged()
                }

        val alertdialog = builder.create()
        alertdialog.show()

    }*/

}