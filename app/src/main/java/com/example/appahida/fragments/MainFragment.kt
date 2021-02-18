package com.example.appahida.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.appahida.R
import com.example.appahida.adapters.ExercicesListAdapter
import com.example.appahida.adapters.RepAdapter
import com.example.appahida.adapters.ViewPagerAdapter
import com.example.appahida.databinding.FragmentMainBinding
import com.example.appahida.onVersionChanged
import com.example.appahida.utils.Utility
import com.example.appahida.utils.onQueryTextChanged
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainFragment : Fragment(), onVersionChanged{

    companion object {
        fun newInstance() = MainFragment()
    }

    lateinit var workoutAdapter : ExercicesListAdapter
    lateinit var repsAdapter : RepAdapter

    lateinit var calendar : HorizontalCalendar
    private var isTodaysWorkoutDone = false
    private var isToday = false

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private val workoutsViewModel : WorkoutViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }


    private var pressedBack = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setHorizontalCalendar()

        viewModel.getExercices(this)

        getAllDates()

    }

    private fun getAllDates() {
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)

        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)

        val datesBetween = datesBetween(startDate, endDate)

        var currentDayPosition = 0
        var dayCounter = 0

        for(date in datesBetween){
            if(DateUtils.isToday(date.timeInMillis)){
                currentDayPosition = dayCounter
            }else{
                dayCounter++
            }
        }

/*        val datesBetween = mutableListOf<Calendar>()

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DATE, 1)

        datesBetween.add(yesterday)
        datesBetween.add(today)
        datesBetween.add(tomorrow)*/

        val adapter = ViewPagerAdapter(this, datesBetween)
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        val viewPager = binding.viewPager
        viewPager.adapter = adapter
        viewPager.setCurrentItem(currentDayPosition, false)

        binding.data.animationDuration = 300
        binding.data.setText(Utility.getDateMonth(datesBetween.get(currentDayPosition).timeInMillis))

        setButtonsListeners()


        val listener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.data.setText(Utility.getDateMonth(datesBetween.get(position).timeInMillis))

                Timber.d("Page selected $position size ${datesBetween.size}")

/*                if(position == 0){
                    val firstDate = datesBetween.get(0)
                    val newDay = Calendar.getInstance()
                    newDay.timeInMillis = firstDate.timeInMillis
                    newDay.add(Calendar.DATE, -1)

                    datesBetween.add(0, newDay)

                    adapter.notifyItemChanged(0)

                }else if(position == datesBetween.size - 1){
                    val lastDate = datesBetween.get(datesBetween.size - 1)
                    val newDay = Calendar.getInstance()
                    newDay.timeInMillis = lastDate.timeInMillis
                    newDay.add(Calendar.DATE, 1)

                    datesBetween.add(newDay)

                    adapter.notifyDataSetChanged()

                    Timber.d("Adding new date ${Utility.getDateMonth(newDay.timeInMillis)}, ${datesBetween.size}")
                }*/
            }

            override fun onPageScrollStateChanged(state: Int) {
                if(state == ViewPager.SCROLL_STATE_IDLE){
                    
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            }
        }

        viewPager.registerOnPageChangeCallback(listener)
    }

    private fun formatCurrentDay(timestamp : Long) : String{
        var returnString = "N/A"
        val now = System.currentTimeMillis()
        if(DateUtils.isToday(timestamp)){
            returnString = "Azi"
        }else if (timestamp > now && (timestamp - now) < TimeUnit.DAYS.toMillis(1)){
            returnString = "Maine"
        }else{
            returnString = Utility.getDateMonth(timestamp)
        }


        return returnString
    }

    private fun setButtonsListeners() {
        val buttonPrevious = binding.back
        val buttonNext = binding.forward
        val viewPager = binding.viewPager

        buttonPrevious.setOnClickListener {
            if(viewPager.currentItem > 0){
                viewPager.setCurrentItem(viewPager.currentItem - 1)
            }
        }

        buttonNext.setOnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem + 1)
        }
    }

    fun datesBetween(start : Calendar, end : Calendar) : MutableList<Calendar>{
        val retList : MutableList<Calendar> = mutableListOf()
        val date: Calendar = start


        while(date.before(end)){
            date.add(Calendar.DATE, 1)

            val nextDay = Calendar.getInstance()
            nextDay.timeInMillis = date.timeInMillis

            retList.add(nextDay)
        }

        return retList
    }


    class ZoomOutPageTransformer : ViewPager2.PageTransformer {

        private val MIN_SCALE = 0.99f
        private val MIN_ALPHA = 0.5f

        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.calendar_button, menu)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    lateinit var alert : AlertDialog

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
}