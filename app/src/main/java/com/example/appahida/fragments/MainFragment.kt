package com.example.appahida.fragments

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.appahida.R
import com.example.appahida.adapters.ExercicesListAdapter
import com.example.appahida.adapters.RepAdapter
import com.example.appahida.constants.Constants.ACTION_START_OR_RESUME
import com.example.appahida.databinding.FragmentMainBinding
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import com.example.appahida.onVersionChanged
import com.example.appahida.services.WorkingService
import com.example.appahida.utils.Utility
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import dagger.hilt.android.AndroidEntryPoint
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.calendar_item_unselected.view.*
import kotlinx.android.synthetic.main.unified_ad_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment(), onVersionChanged, ExercicesListAdapter.FavClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    lateinit var workoutAdapter : ExercicesListAdapter
    lateinit var repsAdapter : RepAdapter

    lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private var isTodaysWorkoutDone = false

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private val workoutsViewModel : WorkoutViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        logMessage("onCreateView")

        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    // TODO: alert has not been initialized
    lateinit var alert : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        logMessage("onCreate")
        super.onCreate(savedInstanceState)

        //loadNativeAds()

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

    private var isWorking = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")

        setupBottomSheet()
        setHorizontalCalendar()

        viewModel.getExercices(this)

        WorkingService.isWorking.observe(viewLifecycleOwner){ workoutOngoing ->
            //binding.fab.isVisible = it
            binding.startWorkout.isVisible = workoutOngoing

            if(workoutOngoing){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            isWorking = workoutOngoing
        }

/*        binding.fab.setOnClickListener {
            if(WorkingService.isWorking.value == true){
                findNavController().navigate(R.id.action_mainFragment_to_workoutEditor)
            }
        }*/

        workoutsViewModel.todaysValue.observe(viewLifecycleOwner){ today ->
            if(today == null){
                workoutsViewModel.insertDay(0, true)
            }else{
                if(today.isWorkoutDone){
                    isTodaysWorkoutDone = true
                    //binding.startWorkout.visibility = View.GONE
                    binding.workoutFinishedCV.visibility = View.VISIBLE
                    binding.finishGroup.visibility = View.GONE
                    binding.durataAntrenament.text = Utility.getFormattedDuration(today.workoutDuration)
                }else{
                    isTodaysWorkoutDone = false
                    binding.workoutFinishedCV.visibility = View.GONE
                    binding.startWorkout.text = "INCEPE ANTRENAMENT"

                    binding.finishGroup.visibility = View.VISIBLE
                }
            }
        }

        workoutsViewModel.exercicesForToday.observe(viewLifecycleOwner){ dayWithExercise ->
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
                }else{
                    val valueAnimator = ValueAnimator.ofInt(currentValue, it)
                    valueAnimator.setDuration(700)
                    valueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener{
                        binding.waterQty.text = it.getAnimatedValue().toString()
                        binding.progressWater.progress = it.getAnimatedValue() as Int
                    })

                    valueAnimator.start()
                }

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

            startWorkout.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Vrei sa incepi antrenamentul ?")
                        .setCancelable(true)
                        .setPositiveButton("Da"){ dialog, id ->
                            if(workoutsViewModel.todaysValue.value?.isWorkoutDone == true){
                                binding.startWorkout.isVisible = false
                            }else{
                                Toast.makeText(requireContext(), "Antrenamentul incepe..", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_mainFragment_to_workoutEditor)
                                sendCommandToService(ACTION_START_OR_RESUME)
                            }
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
                        workoutsViewModel.deleteToday()
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

    private fun setupBottomSheet(){
        val bottomSheet = binding.bottomSheet as NestedScrollView
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        /* show/hide button */
        val showHideButton = binding.toggleButton
        showHideButton?.setOnCheckedChangeListener { _, checked ->
            if(checked){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        /* bottom sheet callback */
        bottomSheetBehavior.addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        showHideButton?.isChecked = false
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        showHideButton?.isChecked = true
                    }

                    else -> Unit
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                System.out.println("sliding $slideOffset")
            }

        })

        if(isWorking){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }else{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheet.isVisible = isWorking
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
                val time = Calendar.getInstance().apply {
                    timeInMillis = date?.timeInMillis!!
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 1)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                viewModel.selectedDate.value = time
                workoutsViewModel.selectedDate.value = time

/*              if (date != null) {
                    binding.month .text = SimpleDateFormat("MMMM", Locale("ro", "RO")).format(date.time)
                }*/

                if(DateUtils.isToday(time)){
                    binding.apply {
                        mesaj.text = "Nu ai un antrenament inca"

                        if(!isTodaysWorkoutDone){
                            if(!editBtn.isVisible){
                                editBtn.isVisible = true
                            }

                            if(!arrow.isVisible){
                                arrow.isVisible = true
                            }

                            if(!mesaj.isVisible){
                                mesaj.isVisible = true
                            }

                        }
                    }
                }else{
                    binding.apply {
                        if(!mesaj.text.equals("Nu te-ai antrenat in aceasta zi")){
                            mesaj.text = "Nu te-ai antrenat in aceasta zi"
                        }

                        if(editBtn.isVisible){
                            editBtn.visibility = View.GONE
                        }

                        if(arrow.isVisible){
                            arrow.visibility = View.GONE
                        }

                        if(adauga.isVisible){
                            adauga.visibility = View.GONE
                        }
                    }
                }

/*                if(DateUtils.isToday(time)){
                    *//* its fine *//*
                        binding.apply {
                            adauga.isVisible = true
                            arrow.isVisible = true
                            //editBtn.isVisible = true
                            mesaj.text = "Nu ai un antrenament inca"
                            finishGroup.visibility = View.GONE
                            binding.startWorkout.visibility = View.VISIBLE
                        }
                }else{
                    binding.apply {
                        adauga.isVisible = false
                        arrow.isVisible = false
                        editBtn.isVisible = false
                        mesaj.text = "Nu te-ai antrenat in aceasta zi"
                    }
                }*/

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

            val thisAlert = builder.create()
            thisAlert.setContentView(R.layout.water_settings)
            thisAlert.show()

/*            builder.create().apply {
                setContentView(R.layout.water_settings)
                show()
            }*/

            val picker : NumberPicker = thisAlert.findViewById(R.id.picker)
            val pickerMl : NumberPicker = thisAlert.findViewById(R.id.picker_2)

            val ml = arrayOf("ml")
            pickerMl.displayedValues = ml

            val values = arrayOf("1500", "2000", "2500", "3000", "3500", "4000", "4500", "5000", "5500", "6000", "6500", "7000", "7500", "8000", "8500", "9000")
            picker.wrapSelectorWheel = true
            picker.displayedValues = values
            picker.minValue = 0
            picker.maxValue = 15


            thisAlert.setOnCancelListener {
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

    override fun startLoading() {
        val builder = AlertDialog.Builder(context)

        val customLayout = layoutInflater.inflate(R.layout.dialog_loading, null)

        builder.setView(customLayout)
            .setTitle("Sincronizare cu baza de date..")
            .setCancelable(false)

        alert = builder.create()
        alert.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alert.show()
    }

    override fun stopLoading() {
        alert.dismiss()
    }

    override fun onFavListener(item: ExercicesWithReps) {
        workoutsViewModel.deleteExercice(item.exercice)
    }

    override fun onAddClick(repsRecyclerView: RepAdapter, exId : Int) {
        repsAdapter = repsRecyclerView
        val builder = AlertDialog.Builder(context)
        val customLayout = layoutInflater.inflate(R.layout.choose_rep_layout, null)

        val inputRep = customLayout.findViewById(R.id.inputCount) as TextInputEditText
        val inputWei = customLayout.findViewById(R.id.inputWeight) as TextInputEditText

        builder.setView(customLayout)
                .setCancelable(true)
                .setPositiveButton("Ok"){ dialog, id ->

                    val rep = Integer.parseInt(inputRep.text.toString())
                    val wei = Integer.parseInt(inputWei.text.toString())

                    workoutsViewModel.addReps(rep, wei, exId)
                }

        val alertdialog = builder.create()
        alertdialog.show()
    }

    private var isListVisible = false

    private fun makeListVisible(){
        if(!isListVisible){
            binding.workoutRecyclerView.visibility = View.VISIBLE
            //binding.gantera.visibility = View.GONE
            binding.mesaj.visibility = View.GONE
            //binding.editBtn.visibility = View.VISIBLE
            binding.adauga.text = "Adauga exercitiu"
            binding.deleteImg.visibility = View.VISIBLE

            if(workoutsViewModel.todaysValue.value?.isWorkoutDone == false){
                binding.startWorkout.visibility = View.VISIBLE
            }else{
                binding.startWorkout.visibility = View.GONE
            }

            isListVisible = true
            Timber.d("List made visible")
        }
    }

    private fun hideList(){
        //TODO : card view flicker
        if(isListVisible){
            binding.deleteImg.visibility = View.GONE
            binding.workoutRecyclerView.visibility = View.GONE
            //binding.gantera.visibility = View.VISIBLE
            binding.mesaj.visibility = View.VISIBLE
            binding.adauga.text = "Adauga un antrenament"
            binding.startWorkout.visibility = View.GONE

            isListVisible = false
            Timber.d("List made invisible")
        }

    }

    var pressedBack = 0L


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

    private fun setSingleLineCalender(){/*
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]*/

        val myCalendarViewManager = object : CalendarViewManager{
            override fun setCalendarViewResourceId(
                    position: Int,
                    date: Date,
                    isSelected: Boolean
            ): Int {

                val cal = Calendar.getInstance()
                cal.time = Date()

                if(isSelected){
                    return R.layout.calendar_item_selected
                }

                return R.layout.calendar_item_unselected
            }

            override fun bindDataToCalendarView(
                    holder: SingleRowCalendarAdapter.CalendarViewHolder,
                    date: Date,
                    position: Int,
                    isSelected: Boolean
            ) {
                holder.itemView.day.text = com.michalsvec.singlerowcalendar.utils.DateUtils.getDayNumber(date)
                holder.itemView.dayName.text = SimpleDateFormat("EE", Locale("Romanian", "Romania")).format(date)
            }
        }




/*        binding.calendarViews.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            futureDaysCount = 30
            pastDaysCount = 10
            includeCurrentDate = true
            init()

        }*/
    }

    private fun loadBannerAd(){
        /*val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.adView.adListener = object : AdListener(){
            override fun onAdFailedToLoad(p0: LoadAdError?) {
                super.onAdFailedToLoad(p0)

                Toast.makeText(requireContext(), "Ad failed ${p0?.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onAdImpression() {
                super.onAdImpression()

                Toast.makeText(requireContext(), "impressed", Toast.LENGTH_SHORT).show()
            }
        }*/
    }

    private fun ads(){
        /*
        val config = RequestConfiguration.Bui+lder().setTestDeviceIds(Arrays.asList("DCB08AD74B27BF7C8694E3ACA6B194C9")).build()
        MobileAds.setRequestConfiguration(config);

        //MobileAds.initialize(context, R.string.test_ad_unit_id.toString())
        val adLoader = AdLoader.Builder(context,  "ca-app-pub-4479200586800321/4400004732")
                .forUnifiedNativeAd { unifiedNativeAd ->
                    object : UnifiedNativeAd.OnUnifiedNativeAdLoadedListener{
                        override fun onUnifiedNativeAdLoaded(p0: UnifiedNativeAd?) {
                            val styles = NativeTemplateStyle.Builder().build()

                            binding.myTemplate.setStyles(styles)
                            binding.myTemplate.setNativeAd(unifiedNativeAd)
                        }

                    }


                }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Toast.makeText(requireContext(), "Add loaded", Toast.LENGTH_SHORT).show()


                }

                override fun onAdOpened() {
                    super.onAdOpened()

                    Toast.makeText(requireContext(), "Add opened", Toast.LENGTH_SHORT).show()
                }

                override fun onAdFailedToLoad(p0: LoadAdError?) {
                    super.onAdFailedToLoad(p0)

                    Toast.makeText(requireContext(), "${p0?.message}", Toast.LENGTH_SHORT).show()
                    Timber.d("ada ${p0?.message}")
                }
            })
            .build()





        adLoader.loadAd(AdRequest.Builder().build())


        //loadBannerAd()
        * */
    }

    private fun logMessage(message : String){
        Timber.d(message)
    }

    private fun loadNativeAds(){
        val builder = AdLoader.Builder(requireContext(), resources.getString(R.string.native_ad_unit_id))

        val adLoader = builder.forUnifiedNativeAd(UnifiedNativeAd.OnUnifiedNativeAdLoadedListener {
/*            val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(resources.getColor(R.color.blackos))).build()

            val template = activity?.findViewById(R.id.my_template) as TemplateView
            template.setStyles(styles)
            template.setNativeAd(it)*/

            val adView = layoutInflater.inflate(R.layout.unified_ad_view, null) as UnifiedNativeAdView

            adView.ad_headline.text = it.headline
            adView.ad_app_icon.load(it.icon.uri)

            adView.setNativeAd(it)
            //adView.removeAllViews()

            //binding.myTemplate?.removeAllViews()
            //binding.myTemplate?.addView(adView)

        }).withAdListener(object : AdListener(){
            override fun onAdFailedToLoad(p0: LoadAdError?) {
                super.onAdFailedToLoad(p0)

                Toast.makeText(requireContext(), "Failed ${p0?.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()

            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}