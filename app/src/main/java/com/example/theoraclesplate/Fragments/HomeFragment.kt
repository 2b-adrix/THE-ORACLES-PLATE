package com.example.theoraclesplate.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.theoraclesplate.R
import com.example.theoraclesplate.adapter.PopularAdaptor


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding: FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }
       override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
           super.onViewCreated(view, savedInstanceState)

           val imageList = ArrayList<SlideModel>()
           imageList,add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
           imageList,add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
           imageList,add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))

           val imageSlider = binding.imageSlider
           imageSlider.setImageList(imageList)
           imageSlider.setImageList(imageList, ScaleTypes.FIT)


           imageSlider.setItemClickListener(object : ItemClickListener {
               override doubleClick(position: Int) {

               }

               override fun onItemSelected(position: Int) {
                   val itemPosition = imageList[position]
                   val itemMessage = "Selected Image $position"
                   Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
               }
       })

           val foodName = listOf("Pizza","Burger","Hotdog","Pizza","Burger","Hotdog","Pizza","Burger","Hotdog")
           val price = listOf("$5","$2","$6","$5","$2","$6","$5","$2","$6")
           val popularFoodImages = listOf(R.drawable.food1,R.drawable.food2,R.drawable.food3,)

           val adapter = PopularAdaptor(foodName,price,popularFoodImages)
           binding.recyclerView.adapter = adapter
       }
    companion object {

    }
}