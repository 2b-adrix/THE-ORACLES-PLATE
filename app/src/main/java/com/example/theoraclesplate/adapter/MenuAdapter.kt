package com.example.theoraclesplate.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.theoraclesplate.DetailsActivity
import com.example.theoraclesplate.databinding.MenuItemBinding

class MenuAdapter(private val menuItemsName: MutableList<String>, private val menuItemPrice:MutableList<String>,private val MenuImage: MutableList<Int>,private val  requireContext: Context): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MenuViewHolder,
        position: Int
    ) {
holder.bind(position)    }



    override fun getItemCount(): Int=menuItemsName.size
    inner class MenuViewHolder (private val binding: MenuItemBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(position)
                }
                //setonClickListener to open details

                val intent = Intent(requireContext, DetailsActivity::class.java)
                intent.putExtra("MenuItemName",menuItemsName.get(position))
                intent.putExtra("MenuItemImage",MenuImage.get(position))
                requireContext.startActivity(intent)
            }
        }
        fun bind(position: Int) {
            binding.apply {
                MenuFoodName.text=menuItemsName[position]
                MenuPrice.text=menuItemPrice[position]
                menuImage.setImageResource(MenuImage[position])

            }

            }

        }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    }

