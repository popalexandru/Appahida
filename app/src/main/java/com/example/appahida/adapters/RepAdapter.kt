package com.example.appahida.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appahida.databinding.RepCountLayoutBinding
import com.example.appahida.objects.RepCount

class RepAdapter() : ListAdapter<RepCount, RepAdapter.CakeViewHolder>(
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

        fun bind(category : RepCount){
            binding.apply {
                repCount.text = category.repNumber.toString()
                repWeight.text = category.repWeight.toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RepCount>() {
        override fun areItemsTheSame(oldItem: RepCount, newItem: RepCount) =
            oldItem.repNumber == newItem.repNumber && oldItem.repWeight == newItem.repWeight

        override fun areContentsTheSame(oldItem: RepCount, newItem: RepCount) =
            oldItem == newItem
    }
}