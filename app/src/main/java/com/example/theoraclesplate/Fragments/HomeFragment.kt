package com.example.theoraclesplate.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.theoraclesplate.MenuBottomSheetFragment
import com.example.theoraclesplate.R
import com.example.theoraclesplate.adapter.PopularAdaptor
import com.example.theoraclesplate.databinding.FragmentHomeBinding
import kotlin.jvm.java


class HomeFragment : Fragment() {

    private lateinit var  binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog= MenuBottomSheetFragment()
bottomSheetDialog.show(parentFragmentManager,"Test")
        }
        return binding.root
    }
       override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
           super.onViewCreated(view, savedInstanceState)

           val imageList = ArrayList<SlideModel>()
           imageList.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
           imageList.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
           imageList.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))

           val imageSlider = binding.imageSlider
           imageSlider.setImageList(imageList)
           imageSlider.setImageList(imageList, ScaleTypes.FIT)


           imageSlider.setItemClickListener(object : ItemClickListener {

               override fun onItemSelected(position: Int) {
                   val itemPosition = imageList[position]
                   val itemMessage = "Selected Image $position"
                   Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
               }
       })

           val foodName = listOf("Pizza","Burger","Hotdog")
           val price = listOf("$5","$2","$6","$5","$2","$6","$5","$2","$6")
           val popularFoodImages = listOf(R.drawable.food1,R.drawable.food2,R.drawable.food3,)

           val adapter = PopularAdaptor(foodName,price,popularFoodImages,requireContext())
           binding.PopularRecycleView.layoutManager = LinearLayoutManager(requireContext())
           binding.PopularRecycleView.adapter = adapter
       }
    companion object {

    }
}