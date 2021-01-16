package com.example.appahida.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appahida.databinding.WorkoutEditorItemBinding
import com.example.appahida.databinding.WorkoutSliderItemBinding
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.exercice_added_item.view.*
import kotlinx.android.synthetic.main.exercice_added_item.view.repsRecyclerView
import kotlinx.android.synthetic.main.workout_slider_item.view.*
import timber.log.Timber

class WorkoutEditorListAdapter(private val context: Context, private val listener : FavClickListener) : ListAdapter<ExercicesWithReps, WorkoutEditorListAdapter.CakeViewHolder>(
    DiffCallback()
){

    val varticalAdapter = RepAdapter()
    private val AD_ITEM = 1
    private val EX_ITEM = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeViewHolder {
        val binding = WorkoutSliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val adview : AdView
        val holder : RecyclerView.ViewHolder

        if(viewType == AD_ITEM){
            adview = AdView(context)
            adview.adSize = AdSize.BANNER

            adview.adUnitId = "ca-app-pub-4479200586800321/3701521801"

            val density = context.resources.displayMetrics.density
            val height = Math.round(AdSize.BANNER.height * density)

            val params = AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height)
            adview.layoutParams = params

            val adRequest = AdRequest.Builder().build()
            adview.loadAd(adRequest)

            //return CakeViewHolder(adview)
        }

        return CakeViewHolder(binding, listener)
    }

    override fun getItemViewType(position: Int): Int {
        if(position % 6 == 3)
            return AD_ITEM
        return EX_ITEM
    }

    override fun onBindViewHolder(holder: CakeViewHolder, position: Int) {
        if(position % 6 != 3){
            val currentCake = getItem(position)

            val repsList = currentCake.reps

            var elAdaptero = RepAdapter()

            holder.itemView.repsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                hasFixedSize()
                adapter = elAdaptero
            }

            holder.itemView.exchangeExercice.setOnClickListener {
                currentCake.exercice.let { it1 -> listener.onExchangeExercice(it1) }
            }

            elAdaptero.submitList(repsList)

            holder.bind(currentCake)
        }
    }

    inner class CakeViewHolder(private val binding : WorkoutSliderItemBinding, private val listener: FavClickListener) : RecyclerView.ViewHolder(binding.root){

        init {

                binding.apply {
                    root.setOnClickListener{
                        val position = adapterPosition
                        if(position != RecyclerView.NO_POSITION){
                            val cake = getItem(position)
                            //listener.onFavListener(cake)
                        }
                    }

                    addReps.setOnClickListener {
                        Timber.d("Add image clicked")

                        getItem(adapterPosition).exercice.exId?.let { it1 -> listener.onAddClick(varticalAdapter, it1) }
                    }
                }
        }

        fun bind(category : ExercicesWithReps){
            binding.apply {
                exName.text = category.exercice.name
                exDesc.text = category.exercice.desc
                exImage.load(category.exercice.image)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ExercicesWithReps>() {
        override fun areItemsTheSame(oldItem: ExercicesWithReps, newItem: ExercicesWithReps) =
            oldItem.exercice.name == newItem.exercice.name

        override fun areContentsTheSame(oldItem: ExercicesWithReps, newItem: ExercicesWithReps) =
            oldItem == newItem
    }

    interface FavClickListener{
        fun onFavListener(item: ExercicesWithReps)
        fun onAddClick(repsRecyclerView: RepAdapter, exId : Int)
        fun onExchangeExercice(exercice : Exercice)
    }
}