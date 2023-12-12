package com.example.foodfolio

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(private var recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onImageButtonClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class RecipeViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        val categoryTextView: TextView = itemView.findViewById(R.id.tvCategory)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        init {
            // Handle item click
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            // Handle delete button click
            deleteButton.setOnClickListener {
                listener.onImageButtonClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.titleTextView.text = currentRecipe.title
        holder.categoryTextView.text = currentRecipe.category

        holder.updateButton.setOnClickListener {
            val toUpdateRecipe = Intent(holder.itemView.context, UpdateRecipe::class.java)
            toUpdateRecipe.putExtra("recipeId", currentRecipe.id)
            holder.itemView.context.startActivity(toUpdateRecipe)
        }
    }

    fun refreshData(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    fun getRecipes(): List<Recipe> {
        return recipes
    }
    override fun getItemCount() = recipes.size
}

