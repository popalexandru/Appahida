package com.example.appahida.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appahida.databinding.ExerciceAddedItemBinding
import com.example.appahida.db.dailyworkoutdb.ExerciseToDo
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.objects.ExerciseToAdd
import timber.log.Timber

class ExercicesListAdapter(private val listener : FavClickListener, private val context : Context) : ListAdapter<Exercice, ExercicesListAdapter.CakeViewHolder>(
    DiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeViewHolder {
        val binding = ExerciceAddedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CakeViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CakeViewHolder, position: Int) {
        val currentCake = getItem(position)
        holder.bind(currentCake)
    }

    inner class CakeViewHolder(private val binding : ExerciceAddedItemBinding, private val listener : FavClickListener) : RecyclerView.ViewHolder(binding.root){

        init {

                binding.apply {
                    val varticalAdapter = RepAdapter()
                    repsRecyclerView.apply {
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        hasFixedSize()
                        adapter = varticalAdapter
                    }

                    root.setOnClickListener{
                        val position = adapterPosition
                        if(position != RecyclerView.NO_POSITION){
                            val cake = getItem(position)
                            listener.onFavListener(cake)
                        }
                    }

                    addRepImage.setOnClickListener {
                        Timber.d("Add image clicked")

                        //binding.addRepImage.visibility = View.GONE
                        binding.adaugaRep.visibility = View.GONE
                        binding.repsRecyclerView.visibility = View.VISIBLE

                        listener.onAddClick(varticalAdapter)
                    }
                }
        }

        fun bind(category : Exercice){
            binding.apply {
                exerciceName.text = category.name
                exerciceCateogry.text = category.desc
                exerciceImg.load(category.image)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Exercice>() {
        override fun areItemsTheSame(oldItem: Exercice, newItem: Exercice) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Exercice, newItem: Exercice) =
            oldItem == newItem
    }

    interface FavClickListener{
        fun onFavListener(item: Exercice)
        fun onAddClick(repsRecyclerView: RepAdapter)
    }
}