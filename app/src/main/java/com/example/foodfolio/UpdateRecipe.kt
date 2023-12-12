package com.example.foodfolio

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.foodfolio.databinding.ActivityUpdateRecipeBinding


class UpdateRecipe : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateRecipeBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var recipeId: Int = -1
    private lateinit var categories: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        recipeId = intent.getIntExtra("recipeId", -1)

        if (recipeId != -1) {
            val existingRecipe = databaseHelper.getRecipeById(recipeId)

            if (existingRecipe != null) {
                categories = databaseHelper.getUniqueCategoriesFromDatabase()

                val spinnerAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.editSpinnerCategory.adapter = spinnerAdapter

                val categoryIndex = categories.indexOf(existingRecipe.category)
                binding.editSpinnerCategory.setSelection(categoryIndex)

                binding.editTitle.setText(existingRecipe.title)
                binding.editIngredients.setText(existingRecipe.ingredients)
                binding.editProcedure.setText(existingRecipe.procedure)

                binding.recipeUpdate.setOnClickListener {
                    val updatedTitle = binding.editTitle.text.toString()
                    val updatedCategory = binding.editSpinnerCategory.selectedItem.toString()
                    val updatedIngredients = binding.editIngredients.text.toString()
                    val updatedProcedure = binding.editProcedure.text.toString()

                    val updatedRecipe = Recipe(
                        recipeId,
                        existingRecipe.userID,
                        updatedTitle,
                        updatedCategory,
                        updatedIngredients,
                        updatedProcedure
                    )
                    databaseHelper.updateRecipe(updatedRecipe)
                    Toast.makeText(this, "Recipe Updated", Toast.LENGTH_SHORT).show()
                    finish()
                    val i = Intent(this, homepageUser::class.java)
                    startActivity(i)
                }
            } else {
                Toast.makeText(this, "Recipe Update Unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backView.setOnClickListener {
            val i = Intent(this, homepageUser::class.java)
            startActivity(i)
        }
    }

}


