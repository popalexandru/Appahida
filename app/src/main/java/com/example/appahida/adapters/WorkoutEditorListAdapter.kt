package com.example.appahida.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appahida.databinding.WorkoutEditorItemBinding
import com.example.appahida.databinding.WorkoutSliderItemBinding
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import kotlinx.android.synthetic.main.exercice_added_item.view.*
import timber.log.Timber

class WorkoutEditorListAdapter() : ListAdapter<ExercicesWithReps, WorkoutEditorListAdapter.CakeViewHolder>(
    DiffCallback()
){

    val varticalAdapter = RepAdapter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeViewHolder {
        val binding = WorkoutSliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val adapter = RepAdapter()
        //binding.repsRecyclerView.adapter = adapter

        return CakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CakeViewHolder, position: Int) {
        val currentCake = getItem(position)

/*        val repsList = currentCake.reps
        var elAdaptero = RepAdapter()

        holder.itemView.repsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            hasFixedSize()
            adapter = elAdaptero
        }

        elAdaptero.submitList(repsList)*/
        holder.bind(currentCake)
    }

    inner class CakeViewHolder(private val binding : WorkoutSliderItemBinding) : RecyclerView.ViewHolder(binding.root){

        init {

                binding.apply {
                    root.setOnClickListener{
                        val position = adapterPosition
                        if(position != RecyclerView.NO_POSITION){
                            val cake = getItem(position)
                            //listener.onFavListener(cake)
                        }
                    }

/*                    addRepImage.setOnClickListener {
                        Timber.d("Add image clicked")

                        getItem(adapterPosition).exercice.exId?.let { it1 -> listener.onAddClick(varticalAdapter, it1) }
                    }*/
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
    }
}