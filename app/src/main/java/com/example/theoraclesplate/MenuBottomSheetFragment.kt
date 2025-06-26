package com.example.theoraclesplate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theoraclesplate.adapter.MenuAdapter
import com.example.theoraclesplate.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.ArrayList


class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMenuBottomSheetBinding.inflate(inflater, container, false)
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        val cartMenuFoodName = listOf("Pizza","Burger","Pasta","Pizza","Burger","Pasta")
        val cartMenuItemPrice = listOf("$5","$2","$6","$5","$2","$6")

        val cartMenuImage = listOf(R.drawable.food1,R.drawable.food2,R.drawable.food3,R.drawable.food1,R.drawable.food2,R.drawable.food3)


        val adaptor = MenuAdapter(
            ArrayList(cartMenuFoodName),
            ArrayList(cartMenuItemPrice),
            ArrayList(cartMenuImage)
        )

        binding.menuRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecycleView.adapter = adaptor
        return binding.root
    }


    companion object {

    }
}