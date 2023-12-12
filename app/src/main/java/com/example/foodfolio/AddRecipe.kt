package com.example.foodfolio

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast

class AddRecipe : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

       databaseHelper = DatabaseHelper(this)

        val title = findViewById<EditText>(R.id.recipeTitle)
        val categSpinner = findViewById<Spinner>(R.id.recipeSpinnerCategory)
        val ingredients = findViewById<EditText>(R.id.recipeIngredients)
        val procedure = findViewById<EditText>(R.id.recipeProcedure)

        val save = findViewById<ImageView>(R.id.recipeSaveBtn)
        val backView = findViewById<ImageView>(R.id.backView)

        val categories = databaseHelper.getUniqueCategoriesFromDatabase()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categSpinner.adapter = adapter

       val userID = getCurrentUserID()

/*        save.setOnClickListener {
            val userID = getCurrentUserID()
            val titleText = title.text.toString()
            val categoryText = categSpinner.selectedItem.toString()
            val ingredientsText = ingredients.text.toString()
            val procedureText = procedure.text.toString()
            val recipe = Recipe(0, userID, titleText, categoryText, ingredientsText, procedureText)
            databaseHelper.insertRecipe(recipe)

            finish()
            Toast.makeText(this, "Recipe Saved", Toast.LENGTH_LONG).show()
        }*/

        save.setOnClickListener {
            val userID = getCurrentUserID()
            val titleText = title.text.toString()
            val categoryText = categSpinner.selectedItem.toString()
            val ingredientsText = ingredients.text.toString()
            val procedureText = procedure.text.toString()

            // validation for empty fields, di magproceed
            if (titleText.isEmpty() || categoryText.isEmpty() || ingredientsText.isEmpty() || procedureText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                // if fields are ano, magadd na sha
                val recipe = Recipe(0, userID, titleText, categoryText, ingredientsText, procedureText)
                databaseHelper.insertRecipe(recipe)

                finish()
                Toast.makeText(this, "Recipe Saved", Toast.LENGTH_LONG).show()
            }
        }

        backView.setOnClickListener {
            val i = Intent(this, homepageUser::class.java)
            startActivity(i)
        }

    }

    private fun getCurrentUserID() : Int {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("USER_ID", -1)
    }
}