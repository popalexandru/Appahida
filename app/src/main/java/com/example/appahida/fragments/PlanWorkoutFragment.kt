package com.example.appahida.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appahida.R
import com.example.appahida.adapters.CategoryAdapter
import com.example.appahida.adapters.ExercicesListAdapter
import com.example.appahida.adapters.OldAdapter
import com.example.appahida.adapters.RepAdapter
import com.example.appahida.databinding.PlanWorkoutBinding
import com.example.appahida.db.dailyworkoutdb.ExerciseToDo
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.objects.RepCount
import com.example.appahida.objects.WorkoutCategory
import com.example.appahida.objects.WorkoutDate
import com.example.appahida.viewmodels.MainViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.exercice_added_item.view.*
import kotlinx.android.synthetic.main.plan_workout.*
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class PlanWorkoutFragment : Fragment(), CategoryAdapter.onCategoryClick, OldAdapter.FavClickListener {
    private var _binding : PlanWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlanWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val startDate = WorkoutDate(Calendar.getInstance())

        binding.dateET.setText(startDate.getCurrentDate())
        binding.dateET.setOnClickListener {
            val datePickerDialog =
                    DatePickerDialog(requireContext(), { view, year, month, day ->
                        dateET.setText(startDate.setDate(day, month, year))
                    }, startDate.year, startDate.month, startDate.day)
            datePickerDialog.show()
        }

        binding.startTimeET.setText(startDate.getCurrentHour())
        binding.startTimeET.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                context,
                { view, hourOfDay, minute ->
                    startTimeET.setText(startDate.setHour(hourOfDay, minute));
                }, startDate.hour, startDate.minute, true
            )
            timePickerDialog.show()
        }

        binding.endTimeET.setOnClickListener {
            val timePickerDialog: TimePickerDialog = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    if (hourOfDay > startDate.hour) {
                        endTimeET.setText(startDate.setEndHour(hourOfDay, minute));
                    } else {
                        endTimeET.setError("")
                    }
                }, startDate.hour, startDate.minute, true
            )
            timePickerDialog.show()
        }

        binding.fab.setOnClickListener {
            //findNavController().navigate(R.id.action_planWorkoutFragment_to_addWorkoutFragment)
        }


        val verticalAdapter = OldAdapter(this, requireContext())

        binding.exercicesRecycler.layoutManager = LinearLayoutManager(context)
        binding.exercicesRecycler.hasFixedSize()
        binding.exercicesRecycler.adapter = verticalAdapter

        viewModel.workoutsFlow.observe(viewLifecycleOwner){
            Timber.d("Observer called ${it.size}")
            if(it.size == 0){
                binding.exercicesRecycler.visibility = View.GONE
            }else{
                binding.exercicesRecycler.visibility = View.VISIBLE
                binding.mesajGol.visibility = View.GONE
                verticalAdapter.submitList(it)
            }
        }

    }

    override fun onCategoryClick(item: WorkoutCategory) {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save_button, menu)
    }

    override fun onFavListener(item: ExerciseToAdd) {
        TODO("Not yet implemented")
    }

    override fun onAddClick(adapter: RepAdapter) {

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

    }
}