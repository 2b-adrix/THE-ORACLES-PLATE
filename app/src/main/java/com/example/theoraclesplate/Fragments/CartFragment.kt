package com.example.theoraclesplate.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theoraclesplate.CongratsBottomSheet
import com.example.theoraclesplate.PayOutActivity
import com.example.theoraclesplate.R
import com.example.theoraclesplate.adapter.CartAdaptor
import com.example.theoraclesplate.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)

        val cartFoodName = listOf("Pizza","Burger","Pasta")
        val cartItemPrice = listOf("$5","$2","$6")
        val cartImage = listOf(R.drawable.food1,R.drawable.food2,R.drawable.food3)
        val adaptor = CartAdaptor(ArrayList(cartFoodName),ArrayList(cartItemPrice),ArrayList(cartImage))
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adaptor


        binding.proceedButton.setOnClickListener {
            var intent = Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }


    companion object {

    }
}