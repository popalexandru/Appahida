package com.example.appahida.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter
import com.example.appahida.R
import com.example.appahida.adapters.CategoryAdapter
import com.example.appahida.adapters.ExercicesAdapter
import com.example.appahida.constants.Constants
import com.example.appahida.databinding.FragmentAddworkoutBinding
import com.example.appahida.db.exercicesDB.ExerciseItem
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.objects.WorkoutCategory
import com.example.appahida.utils.onQueryTextChanged
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.unified_ad_view.view.*
import timber.log.Timber

@AndroidEntryPoint
class AddWorkoutFragment : Fragment(), CategoryAdapter.onCategoryClick, ExercicesAdapter.FavClickListener {
    private var _binding : FragmentAddworkoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private  var exerciceExchange = false
    private lateinit var exToDelete : Exercice

    private val viewModel: MainViewModel by activityViewModels()
    private val workoutsViewModel : WorkoutViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

/*        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_addWorkoutFragment_to_mainFragment)
        }

        callback.isEnabled = true*/

        readIntent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAddworkoutBinding.inflate(layoutInflater)
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
                setItemViewCacheSize(3)
                adapter = adaptere

                //loadNativeAds(adaptere)

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

        Timber.d("Inserting exercice ${exerciceItem.name}")

        viewModel.searchQuery.value = ""
        viewModel.categoryQuery.value = ""

        if(exerciceExchange){
            workoutsViewModel.deleteExercice(exToDelete)
            //findNavController().navigate(R.id.action_addWorkoutFragment_to_workoutEditor)
        }else{
            //findNavController().navigate(R.id.action_addWorkoutFragment_to_mainFragment)
            findNavController().navigateUp()
        }

    }

    override fun onHelpClick(item: ExerciseItem) {
        val builder = AlertDialog.Builder(context)
        val customLayout = layoutInflater.inflate(R.layout.dialog_exercice_description, null)

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

    private fun loadNativeAds(categoryAdapter: ExercicesAdapter) {
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
        "small").adItemInterval(10).build()


        binding.veritcalRecycler.adapter = adapter
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

    fun readIntent(){
        val argument = arguments?.getString("param")
        if(argument.equals(Constants.EXERCICE_EXCHANGE)){
            val exId = arguments?.getSerializable("exId") as Exercice

            exerciceExchange = argument?.isNotEmpty() == true && argument.equals(Constants.EXERCICE_EXCHANGE)

            exToDelete = exId

            view?.let { Snackbar.make(it, "Alege un exercitiu in locul ${exToDelete.name}", Snackbar.LENGTH_LONG).show() }
            Toast.makeText(requireContext(), "Alege un exercitiu in locul ${exToDelete.name}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}