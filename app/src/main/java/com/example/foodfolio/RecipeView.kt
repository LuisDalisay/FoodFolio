package com.example.foodfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class RecipeView : AppCompatActivity() {
    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onImageButtonClick(position: Int)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_view)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val recipeTitleTextView = findViewById<TextView>(R.id.RecipeTitle)
        val recipeCategoryTextView = findViewById<TextView>(R.id.RecipeCategory)
        val recipeIngListTextView = findViewById<TextView>(R.id.RecipeIngList)
        val recipeProcListTextView = findViewById<TextView>(R.id.recipeProcList)

        backBtn.setOnClickListener {
            val i = Intent(this, homepageUser::class.java)
            startActivity(i)
        }

        // Retrieve recipe ID from the intent
        val recipeId = intent.getIntExtra("recipeId", -1)

        // Fetch recipe details from the database using the ID
        if (recipeId != -1) {
            val databaseHelper = DatabaseHelper(this)
            val recipe = databaseHelper.getRecipeById(recipeId)

            // Display recipe details
            recipe?.let {
                recipeTitleTextView.text = it.title
                recipeCategoryTextView.text = "${it.category}"
                recipeIngListTextView.text = "${it.ingredients}"
                recipeProcListTextView.text = "${it.procedure}"
            }
        }
    }
}