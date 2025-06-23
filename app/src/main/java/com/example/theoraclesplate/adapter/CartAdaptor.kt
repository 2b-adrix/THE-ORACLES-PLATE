package com.example.theoraclesplate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.theoraclesplate.databinding.CartItemBinding

class CartAdaptor (private val CartItems: MutableList<String>,private val CartItemPrice:MutableList<String>,private var cartImage : MutableList<Int>): RecyclerView.Adapter<CartAdaptor.CartViewHolder>() {
private val itemQuantities = IntArray(CartItems.size){1}
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartViewHolder(binding)
    }



    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }
    override fun getItemCount(): Int = CartItems.size

   inner class CartViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply{
                val quantity = itemQuantities[position]
                cartFoodName.text = CartItems[position]
                cartItemPrice.text = CartItemPrice[position]
                cartimage.setImageResource(cartImage[position].toInt())
                 cartItemQuantity.text = quantity.toString()


                minusbutton.setOnClickListener {
                    decreaseQuantity(position)
                }
                    plusbutton.setOnClickListener {
                       increaseQuantity(position)
                    }

                trashbutton.setOnClickListener {
                  val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }

            }
        }
       private fun increaseQuantity(position: Int) {
           if (itemQuantities[position] < 10) {
               itemQuantities[position]++
               binding.cartItemQuantity.text = itemQuantities[position].toString()
           }
       }
       private fun decreaseQuantity(position: Int) {
           if (itemQuantities[position] > 1) {
               itemQuantities[position]--
               binding.cartItemQuantity.text = itemQuantities[position].toString()
           }
       }
                      private  fun deleteItem(position: Int) {
                            CartItems.removeAt(position)
                            CartItemPrice.removeAt(position)
                            cartImage.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, CartItems.size)
                        }
                    }



            }


