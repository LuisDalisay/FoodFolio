package com.example.foodfolio.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfolio.R
import com.example.foodfolio.activities.CategoryMealsActivity
import com.example.foodfolio.activities.MainActivity
import com.example.foodfolio.activities.MealActivity
import com.example.foodfolio.adapters.MealsAdapter
import com.example.foodfolio.adapters.MostPopularAdapter
import com.example.foodfolio.databinding.FragmentSearchBinding
import com.example.foodfolio.fragments.HomeFragment.Companion.MEAL_ID
import com.example.foodfolio.fragments.HomeFragment.Companion.MEAL_NAME
import com.example.foodfolio.fragments.HomeFragment.Companion.MEAL_THUMB
import com.example.foodfolio.pojo.Meal
import com.example.foodfolio.pojo.MealsByCategory
import com.example.foodfolio.pojo.MealsByCategoryList
import com.example.foodfolio.retrofit.RetrofitInstance
import com.example.foodfolio.viewModel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel:HomeViewModel
    private lateinit var searchRecyclerviewAdapter : MealsAdapter

    companion object {
        const val  MEAL_ID = "com.example.foodfolio.fragments.idMeal"
        const val  MEAL_NAME = "com.example.foodfolio.fragments.idName"
        const val  MEAL_THUMB = "com.example.foodfolio.fragments.idThumb"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()

        binding.imgSearchArrow.setOnClickListener { searchMeals() }

        observeSearchedMealsLiveData()
        setOnMealCardClick()

        var searchJob: Job?=null
        binding.edSearchBox.addTextChangedListener { searchQuery ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(500)
                viewModel.searchMeals(searchQuery.toString())
            }
        }
    }

    private fun setOnMealCardClick() {
        searchRecyclerviewAdapter.onItemClick = { meal ->
            val intent = Intent(activity,MealActivity::class.java)
            intent.putExtra(MEAL_ID,meal.idMeal)
            intent.putExtra(MEAL_NAME,meal.strMeal)
            intent.putExtra(MEAL_THUMB,meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun observeSearchedMealsLiveData() {
        viewModel.observeSearchMealsLiveData().observe(viewLifecycleOwner, Observer { mealsList ->
            searchRecyclerviewAdapter.differ.submitList(mealsList)
        })
    }

    private fun searchMeals() {
        val searchQuery = binding.edSearchBox.text.toString()
        if (searchQuery.isNotEmpty()) {
            viewModel.searchMeals(searchQuery)
        }
    }

    private fun prepareRecyclerView() {
        searchRecyclerviewAdapter = MealsAdapter()
        binding.rvSearchedMeals.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = searchRecyclerviewAdapter
        }
    }

}