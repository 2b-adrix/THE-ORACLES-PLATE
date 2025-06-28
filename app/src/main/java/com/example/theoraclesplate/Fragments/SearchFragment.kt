package com.example.theoraclesplate.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theoraclesplate.R
import com.example.theoraclesplate.adapter.MenuAdapter
import com.example.theoraclesplate.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val originalMenuFoodName = listOf("Pizza", "Burger", "Salad", "Pasta", "Sushi","Pizza")
      private lateinit var adaptor: MenuAdapter
   private val OriginalMenuItemPrice = listOf("$5","$2","$6","$5","$2","$6")

   private val OriginalMenuImage = listOf(
        R.drawable.food1,
        R.drawable.food2,
        R.drawable.food3,
        R.drawable.food1,
        R.drawable.food2,
        R.drawable.food3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
     private val filteredMenuFoodName = mutableListOf<String>()
    private val filteredMenuItemPrices = mutableListOf<String>()
    private val filteredMenuImages = mutableListOf<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        adaptor = MenuAdapter(filteredMenuFoodName,filteredMenuItemPrices,filteredMenuImages)
        binding.menuRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adaptor


        //setup search view

        setupSearchView()

        // show All menus Items

        showAllMenu()
        return binding.root
    }

    private fun showAllMenu() {
        filteredMenuFoodName.clear()
        filteredMenuItemPrices.clear()
        filteredMenuImages.clear()

        filteredMenuFoodName.addAll(originalMenuFoodName)
        filteredMenuItemPrices.addAll(OriginalMenuItemPrice)
        filteredMenuImages.addAll(OriginalMenuImage)


        adaptor.notifyDataSetChanged()
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener {
         override fun onQueryTextSubmit(query: String): Boolean {
             filterMenuItems(query)
             return true
         }



         override fun onQueryTextChange(newText: String): Boolean {
         filterMenuItems(newText)
         return true


         } })
    }
    private fun filterMenuItems(query: String) {
        filteredMenuFoodName.clear()
        filteredMenuItemPrices.clear()
        filteredMenuImages.clear()

        originalMenuFoodName.forEachIndexed { index, foodName ->
            if (foodName.contains(query, ignoreCase = true)) {
                filteredMenuFoodName.add(foodName)
                filteredMenuItemPrices.add(OriginalMenuItemPrice[index])
                filteredMenuImages.add(OriginalMenuImage[index])

            }

        }
          adaptor.notifyDataSetChanged()

    }


    companion object {

    }
}