package com.example.appahida.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appahida.databinding.RepCountLayoutBinding
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import com.example.appahida.db.workoutsdb.Reps
import com.example.appahida.objects.RepCount

class RepAdapter() : ListAdapter<Reps, RepAdapter.CakeViewHolder>(
    DiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeViewHolder {
        val binding = RepCountLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CakeViewHolder, position: Int) {
        val currentCake = getItem(position)
        holder.bind(currentCake)
    }

    inner class CakeViewHolder(private val binding : RepCountLayoutBinding) : RecyclerView.ViewHolder(binding.root){

/*        init {
                binding.apply {
                    root.setOnClickListener{
                        val position = adapterPosition
                        if(position != RecyclerView.NO_POSITION){
                            val cake = getItem(position)
                            listener.onFavListener(cake)
                        }
                    }
                }
        }*/

        fun bind(category : Reps){
            binding.apply {
                repCount.text = category.repCount.toString()
                repWeight.text = category.repWeight.toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Reps>() {
        override fun areItemsTheSame(oldItem: Reps, newItem: Reps) =
            oldItem.repCount == newItem.repCount && oldItem.repWeight == newItem.repWeight

        override fun areContentsTheSame(oldItem: Reps, newItem: Reps) =
            oldItem == newItem
    }
}