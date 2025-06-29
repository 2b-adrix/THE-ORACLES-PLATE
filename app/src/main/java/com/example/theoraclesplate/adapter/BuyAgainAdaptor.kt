package com.example.theoraclesplate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.theoraclesplate.databinding.BuyAgainItemBinding

class BuyAgainAdaptor(private val buyAgainFoodName:ArrayList<String>,
                      private val buyAgainFoodPrice: ArrayList<String> ,
                      private val buyAgainImage: ArrayList<Int> ): RecyclerView.Adapter<BuyAgainAdaptor.BuyAgainViewHolder>()
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuyAgainViewHolder {
val binding= BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BuyAgainViewHolder,
        position: Int
    ) {
        holder.bind(buyAgainFoodName[position],buyAgainFoodPrice[position],buyAgainImage[position])
    }

    override fun getItemCount(): Int=  buyAgainFoodName.size

    class BuyAgainViewHolder (private val binding:BuyAgainItemBinding):RecyclerView.ViewHolder(binding.root)
    {
        fun bind(FoodName: String, FoodPrice: String, FoodImage: Int) {
binding. buyAgainFoodName.text= FoodName
        binding.buyAgainFoodPrice.text=FoodPrice
        binding.buyAgainFoodImage.setImageResource(FoodImage)

        }

    }
}