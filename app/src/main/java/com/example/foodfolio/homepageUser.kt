package com.example.foodfolio

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfolio.databinding.ActivityHomepageuserBinding
import androidx.core.content.ContentProviderCompat.requireContext


class homepageUser : Fragment(), RecipeAdapter.OnItemClickListener {

    private lateinit var binding: ActivityHomepageuserBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityHomepageuserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loggedInUserId = getCurrentUserID()

        databaseHelper = DatabaseHelper(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val recipes = databaseHelper.getAllRecipes(loggedInUserId)

        recipeAdapter = RecipeAdapter(recipes)
        binding.recipeRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recipeRecycler.adapter = recipeAdapter

        recipeAdapter.setOnItemClickListener(this)

        binding.btnAddRecipe.setOnClickListener {
            val i = Intent(requireContext(), AddRecipe::class.java)
            startActivity(i)
        }

        val btnLogout: ImageView = requireView().findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(requireContext(), LogIn::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "Log Out Successful", Toast.LENGTH_LONG).show()
            requireActivity().finish()
        }
    }


    override fun onItemClick(position: Int) {
        val selectedRecipe = recipeAdapter.getRecipes()[position]

        // Open RecipeView activity with details of the clicked recipe
        val intent = Intent(requireContext(), RecipeView::class.java)
        intent.putExtra("recipeId", selectedRecipe.id)
        startActivity(intent)
    }

    override fun onImageButtonClick(position: Int) {
        val selectedRecipe = recipeAdapter.getRecipes()[position]

        databaseHelper.deleteRecipe(selectedRecipe.id)


        val loggedInUserId = getCurrentUserID()
        val updatedRecipes = databaseHelper.getAllRecipes(loggedInUserId)
        recipeAdapter.refreshData(updatedRecipes)
    }

    override fun onResume() {
        super.onResume()
        val loggedInUserId = getCurrentUserID()
        val recipes = databaseHelper.getAllRecipes(loggedInUserId)
        recipeAdapter.refreshData(recipes)
    }

    private fun getCurrentUserID(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("USER_ID", -1)
    }
}