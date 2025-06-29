package com.example.theoraclesplate.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theoraclesplate.R
import com.example.theoraclesplate.adapter.BuyAgainAdaptor
import com.example.theoraclesplate.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdaptor: BuyAgainAdaptor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)
            setupRecyclerView()
            return binding.root
    }

    private fun setupRecyclerView()
    {
       val buyAgainFoodName = arrayListOf("Food 1","Food 2","Food 3")
       val buyAgainFoodPrice = arrayListOf("$10","$20","$30")
       val buyAgainFoodImage = arrayListOf(R.drawable.food2,R.drawable.food3,R.drawable.food1)
        buyAgainAdaptor = BuyAgainAdaptor(buyAgainFoodName,buyAgainFoodPrice,buyAgainFoodImage)
        binding.BuyAgainRecyclerView.adapter=buyAgainAdaptor
        binding.BuyAgainRecyclerView.layoutManager= LinearLayoutManager(requireContext())
    }
    companion object {

    }
}