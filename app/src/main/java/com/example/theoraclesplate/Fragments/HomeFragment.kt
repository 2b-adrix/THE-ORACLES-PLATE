package com.example.theoraclesplate.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.fragment.app.Fragment
import com.example.theoraclesplate.R
import com.example.theoraclesplate.databinding.FragmentHomeBinding
import com.google.android.material.slider.Slider


class HomeFragment : Fragment() {
private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            val imageList = ArrayList<SlideModel>()
            imageList.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
            imageList.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
            imageList.add(SlideModel(R.drawable.banner3,ScaleTypes.FIT))

            val imageSlider = binding.imageSlider
            imageSlider.setImageList(imageList)
            imageSlider.setImageList(imageList, ScaleTypes.FIT)

    }

    companion object {

    }
}