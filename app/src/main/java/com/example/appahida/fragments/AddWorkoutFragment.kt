package com.example.appahida.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.appahida.R
import com.example.appahida.adapters.CategoryAdapter
import com.example.appahida.adapters.ExercicesAdapter
import com.example.appahida.databinding.AddWorkoutBinding
import com.example.appahida.db.exercicesDB.ExerciseItem
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.objects.WorkoutCategory
import com.example.appahida.utils.onQueryTextChanged
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWorkoutFragment : Fragment(), CategoryAdapter.onCategoryClick, ExercicesAdapter.FavClickListener {
    private var _binding : AddWorkoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private val viewModel: MainViewModel by activityViewModels()
    private val workoutsViewModel : WorkoutViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_addWorkoutFragment_to_mainFragment)
        }

        callback.isEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = AddWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val verticalAdapter = ExercicesAdapter(this)

        viewModel.exercices.observe(viewLifecycleOwner){
            verticalAdapter.submitList(it)
        }

        setHorizontalRecyclerview()
        setVerticalRecyclerview(verticalAdapter)
    }

    private fun setVerticalRecyclerview(adaptere: ExercicesAdapter){
        binding.apply {
            veritcalRecycler.apply{
                layoutManager = LinearLayoutManager(context)
                hasFixedSize()
                adapter = adaptere

                addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            }
        }
    }

    private fun setHorizontalRecyclerview(){
        val categoryList : MutableList<WorkoutCategory> = mutableListOf()
        val categoryAdapter = CategoryAdapter(this)

        categoryList.apply {
            add(WorkoutCategory("Piept", "https://firebasestorage.googleapis.com/v0/b/revducts-dae30.appspot.com/o/abs.png?alt=media&token=fd0f5ee2-6527-4539-8317-cb279d71af91"))
            add(WorkoutCategory("Brate", "https://firebasestorage.googleapis.com/v0/b/revducts-dae30.appspot.com/o/biceps.png?alt=media&token=b83d6650-bef8-4823-9d3c-780d598a14ed"))
            add(WorkoutCategory("Umeri", "https://firebasestorage.googleapis.com/v0/b/revducts-dae30.appspot.com/o/shoulder.png?alt=media&token=0c6c7a71-a057-4a16-9f43-87c354e9c246"))
            add(WorkoutCategory("Spate", "https://firebasestorage.googleapis.com/v0/b/revducts-dae30.appspot.com/o/back.png?alt=media&token=20be0775-8d6d-4381-825f-3fe814fc34eb"))
            add(WorkoutCategory("Abdomen", "https://firebasestorage.googleapis.com/v0/b/revducts-dae30.appspot.com/o/abs.png?alt=media&token=fd0f5ee2-6527-4539-8317-cb279d71af91"))
            add(WorkoutCategory("Cardio", ""))
            add(WorkoutCategory("Picioare", ""))
        }

        binding.apply {
            horizontalRecycler.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                hasFixedSize()
                adapter = categoryAdapter
            }
        }

        categoryAdapter.submitList(categoryList)
    }

    override fun onCategoryClick(item: WorkoutCategory) {
        if(viewModel.categoryQuery.value == item.name){
            viewModel.setCategoryQuery("")
        }else{
            viewModel.setCategoryQuery(item.name)
        }
    }

    /* when user clicks on a database exercice */
    override fun onFavListener(item: ExerciseItem) {
        val exerciceItem = ExerciseToAdd(item.name, item.picture, item.description, item.category, null)

        workoutsViewModel.insertExercice(exerciceItem)
        viewModel.searchQuery.value = ""
        viewModel.categoryQuery.value = ""
        findNavController().navigate(R.id.action_addWorkoutFragment_to_mainFragment)
    }

    override fun onHelpClick(item: ExerciseItem) {
        val builder = AlertDialog.Builder(context)
        val customLayout = layoutInflater.inflate(R.layout.exercise_helper, null)

        val exName : TextView = customLayout.findViewById(R.id.exTitle)
        val exImg : ImageView = customLayout.findViewById(R.id.exImg)
        val exDesc : TextView = customLayout.findViewById(R.id.exDesc)


        exName.text = item.name
        exImg.load(item.picture)
        exDesc.text = item.description

        builder.setView(customLayout)
            .setCancelable(true)

        val alertdialog = builder.create()
        alertdialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_button, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged { inputString ->
            viewModel.setSearchQuery(inputString)
        }

        val pendingQuery = viewModel.categoryQuery.value

        if(pendingQuery.isNotEmpty()){
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }
    }
}