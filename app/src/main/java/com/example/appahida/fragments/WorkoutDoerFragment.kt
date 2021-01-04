package com.example.appahida.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.appahida.R
import com.example.appahida.adapters.*
import com.example.appahida.constants.Constants.ACTION_PAUSE_SERVICE
import com.example.appahida.constants.Constants.ACTION_START_OR_RESUME
import com.example.appahida.constants.Constants.ACTION_STOP_SERVICE
import com.example.appahida.databinding.WorkoutDoerLayoutBinding
import com.example.appahida.services.WorkingService
import com.example.appahida.utils.Utility
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutDoerViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WorkoutDoerFragment : Fragment(){
    private var _binding : WorkoutDoerLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutViewModel by activityViewModels()
    private var curTimeInMilis = 0L
    private var isWorking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_workoutEditor_to_mainFragment)
        }
        callback.isEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = WorkoutDoerLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterHor = WorkoutEditorListAdapter()

        WorkingService.timeWorkedInMilliseconds.observe(viewLifecycleOwner){
            curTimeInMilis = it
            val formattedTime = Utility.getFormattedStopWatchTime(it)
            binding.seconds.text = formattedTime
        }

        WorkingService.isWorking.observe(viewLifecycleOwner){
            isWorking = it
            if(isWorking){
                binding.startTimer.setImageResource(R.drawable.ic_baseline_pause_24)
            }else{
                binding.startTimer.setImageResource(R.drawable.ic_start)
            }
        }

        viewModel.exercicesForToday.observe(viewLifecycleOwner){
            adapterHor.submitList(it)
        }

        binding.apply {

            horRecycler.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                hasFixedSize()
                adapter = adapterHor
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
                WorkingService.isWorking.postValue(false)

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Vrei sa inchei antrenamentul ?")
                        .setCancelable(true)
                        .setPositiveButton("Da"){ dialog, id ->
                            findNavController().navigate(R.id.action_workoutEditor_to_mainFragment)
                            sendCommandToService(ACTION_STOP_SERVICE)
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

}