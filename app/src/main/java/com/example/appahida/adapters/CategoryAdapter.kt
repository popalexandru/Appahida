package com.example.appahida.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appahida.databinding.WorkoutTypeItemBinding
import com.example.appahida.objects.WorkoutCategory
import kotlinx.android.synthetic.main.workout_type_item.view.*

class CategoryAdapter(private val listener : onCategoryClick) : ListAdapter<WorkoutCategory, CategoryAdapter.CakeViewHolder>(
    DiffCallback()
){
    var selectedPosition = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeViewHolder {
        val binding = WorkoutTypeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CakeViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CakeViewHolder, position: Int) {
        val currentCake = getItem(position)
        holder.layout.setOnClickListener {

           // val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                val cake = getItem(position)
                listener.onCategoryClick(cake)
            }
            if(selectedPosition.equals(currentCake.name)){
                selectedPosition = ""
            }else{
                selectedPosition = currentCake.name
            }
            notifyDataSetChanged()
        }

        if(selectedPosition.equals(currentCake.name)){
            holder.layout.separator.setBackgroundColor(Color.parseColor("#911F1F"))
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                holder.layout.outlineSpotShadowColor = Color.parseColor("#911F1F")
                holder.layout.elevation = 10F
            }
        }else{
            //holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.layout.separator.setBackgroundColor(Color.parseColor("#000000"))

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                holder.layout.outlineSpotShadowColor = Color.parseColor("#FFFFFF")
            }
        }

/*        if(selectedPosition.equals(currentCake.name)){
            holder.layout.setBackgroundColor(Color.parseColor("#CCCCCC"))
        }*/

        holder.bind(currentCake)
    }

    inner class CakeViewHolder(private val binding : WorkoutTypeItemBinding, private val listener : onCategoryClick) : RecyclerView.ViewHolder(binding.root){
        val layout = binding.layout

/*        init {
                binding.apply {
                    root.setOnClickListener{
                        val position = adapterPosition
                        if(position != RecyclerView.NO_POSITION){
                            val cake = getItem(position)
                            listener.onCategoryClick(cake)
                        }
                    }
                }
        }*/

        fun bind(category : WorkoutCategory){
            binding.apply {
                categoryTitle.text = category.name
                categoryImage.load(category.imageURL)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WorkoutCategory>() {
        override fun areItemsTheSame(oldItem: WorkoutCategory, newItem: WorkoutCategory) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: WorkoutCategory, newItem: WorkoutCategory) =
            oldItem == newItem
    }

    interface onCategoryClick{
        fun onCategoryClick(item: WorkoutCategory)
    }
}
/*
class CakesAdapter(private var favoritesItem: List<CakeDBItem>, private var context: Context, private val listener : FavClickListener) :

    RecyclerView.Adapter<CakesAdapter.ViewHolder>() {
        inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

            val name : TextView = itemView.findViewById(R.id.name)
            val quantity : TextView = itemView.findViewById(R.id.quantity)
            val done : ImageView = itemView.findViewById(R.id.im_done)
            val address : TextView = itemView.findViewById(R.id.address)
            val date : TextView = itemView.findViewById(R.id.date)
            val time : TextView = itemView.findViewById(R.id.time)
            val sortiment : TextView = itemView.findViewById(R.id.sortiment)
            val timeLeft : TextView = itemView.findViewById(R.id.timeLeft)
            val phone : TextView = itemView.findViewById(R.id.tv_phone)
            val layout : ConstraintLayout = itemView.findViewById(R.id.layout)

            init {
                itemView.setOnClickListener {
                    val position : Int = adapterPosition
                    listener.onFavListener(favoritesItem[position])
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakesAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cakeitem, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return favoritesItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = favoritesItem[position].name
        holder.quantity.text = favoritesItem[position].quantity
        holder.sortiment.text = favoritesItem[position].type
        holder.address.text = favoritesItem[position].address
        holder.time.text = favoritesItem[position].time
        holder.date.text = favoritesItem[position].date
        holder.timeLeft.text = favoritesItem[position].timeLeft
        holder.phone.text = favoritesItem[position].phone

        if(favoritesItem[position].done){
            holder.done.visibility = View.VISIBLE

            holder.layout.setBackgroundColor(Color.parseColor("#80b089"))
            holder.layout.alpha = 0.8F
        }
    }

    interface FavClickListener{
        fun onFavListener(item: CakeDBItem)
    }

}*/