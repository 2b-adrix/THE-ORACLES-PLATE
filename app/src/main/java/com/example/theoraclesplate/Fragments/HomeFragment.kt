package com.example.theoraclesplate.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.theoraclesplate.R
import com.example.theoraclesplate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            val imageList = ArrayList<SlideModel>()
            imageList.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
            imageList.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
            imageList.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))

            val imageSlider = binding.imageSlider
            imageSlider.setImageList(imageList)
            imageSlider.setImageList(imageList, ScaleTypes.FIT)


        imageSlider.setItemClickListener(object : ItemClickListener {


            override fun doubleClick(position: Int) {

            }

            override fun onItemSelected(position: Int) {
                val itemPosinList: Int = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }

        }
        }

    }

    companion object {

    }
}