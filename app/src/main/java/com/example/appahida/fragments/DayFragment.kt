package com.example.appahida.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appahida.R
import com.example.appahida.adapters.ExercicesListAdapter
import com.example.appahida.adapters.RepAdapter
import com.example.appahida.constants.Constants
import com.example.appahida.databinding.DayFragmentBinding
import com.example.appahida.databinding.FragmentMainBinding
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import com.example.appahida.utils.Utility
import com.example.appahida.viewmodels.DayViewModel
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import com.google.firebase.database.collection.LLRBNode
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DayFragment(private val timestamp : Long) : Fragment(){

    private var _binding : DayFragmentBinding? = null
    private val binding get() = _binding!!

    private var isToday = false
    private val isTomorrow = false

    private val dayViewModel: DayViewModel by viewModels()
    private val workoutsViewModel : WorkoutViewModel by viewModels()

    lateinit var workoutAdapter : ExercicesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isToday = DateUtils.isToday(timestamp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DayFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutsViewModel.selectedDate.value = timestamp

        Timber.d("onViewCreated called for ${Utility.getDateMonth(timestamp)}")

/*        val date = Utility.getDateString(timestamp)

        binding.text.text = date
        println("Setting text for $date")*/

        getTodayText()

        if(!isToday){
            binding.fab.hide()
        }

        setObservers()
    }

    private fun setObservers() {
        binding.apply {

            dayViewModel.getTodaysRecord(timestamp).observe(viewLifecycleOwner){ day ->
                if(day == null){
                    dayViewModel.insertDay(timestamp)
                    Timber.d("Creating ${Utility.getDateMonth(timestamp)}")
                }else{
                    Timber.d("${Utility.getDateMonth(timestamp)} exists")
                }
            }

            dayViewModel.getDayWorkout(timestamp).observe(viewLifecycleOwner){ dayWithExercices ->
                if(dayWithExercices != null){
                    Timber.d("${dayWithExercices.exercices.size}")

                    val context = requireContext()
                    workoutAdapter = ExercicesListAdapter()

                    binding.apply{
                        workoutRecyclerView.apply {
                            layoutManager = LinearLayoutManager(context)
                            //hasFixedSize()
                            setItemViewCacheSize(3)
                            adapter = workoutAdapter
                            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
                        }
                    }

                }else{
                    Timber.d("List is null for ${Utility.getDateMonth(timestamp)}")
                }
            }

            fab.setOnClickListener {
                if(isToday){
                    val bundle = bundleOf("timestamp" to timestamp)
                    bundle.putString("param", Constants.ADD_WORKOUT_FOR_TODAY)

                    findNavController().navigate(R.id.action_mainFragment_to_addWorkoutFragment, bundle)
                }else{
                    fab.hide()
                }
            }

        }
    }

    private fun getTodayText() {
        val now = System.currentTimeMillis()
        binding.text.visibility = View.VISIBLE

        if(DateUtils.isToday(timestamp)){
            binding.text.text = "Azi"
        }else if (timestamp > now && (timestamp - now) < TimeUnit.DAYS.toMillis(1)){
            binding.text.text = "Maine"
        }else if (now > timestamp && (now - timestamp) < TimeUnit.DAYS.toMillis(1)){
            binding.text.text = "Ieri"
        }else{
            binding.text.visibility = View.GONE
        }
    }

    private fun logMessage(message : String){
        Timber.d("Load")
    }
}