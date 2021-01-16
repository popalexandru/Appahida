package com.example.appahida.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appahida.R
import com.example.appahida.databinding.ExerciceItemBinding
import com.example.appahida.db.exercicesDB.ExerciseItem

class ExercicesAdapter(private val listener : FavClickListener) : ListAdapter<ExerciseItem, ExercicesAdapter.CakeViewHolder>(
    DiffCallback()
){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeViewHolder {
        val binding = ExerciceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CakeViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CakeViewHolder, position: Int) {
        val currentCake = getItem(position)
        holder.bind(currentCake)
    }

    inner class CakeViewHolder(private val binding : ExerciceItemBinding, private val listener : FavClickListener) : RecyclerView.ViewHolder(binding.root){

        init {
                binding.apply {
                    root.setOnClickListener{
                        val position = adapterPosition
                        if(position != RecyclerView.NO_POSITION){
                            val cake = getItem(position)
                            listener.onFavListener(cake)
                        }
                    }

                    details.setOnClickListener {
                        listener.onHelpClick(getItem(adapterPosition))
                    }
                }
        }

        fun bind(category : ExerciseItem){
            binding.apply {
                exerciceName.text = category.name
                exerciceCateogry.text = category.category
                workoutDesc.text = category.description
                exerciceImg.load(category.picture)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ExerciseItem>() {
        override fun areItemsTheSame(oldItem: ExerciseItem, newItem: ExerciseItem) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: ExerciseItem, newItem: ExerciseItem) =
            oldItem == newItem
    }

    interface FavClickListener{
        fun onFavListener(item: ExerciseItem)
        fun onHelpClick(item : ExerciseItem)
    }
}