package com.example.theoraclesplate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun CartScreen(rootNavController: NavController) {
    val cartItems = remember { mutableStateListOf(
        CartItemData("Pizza", "$5", R.drawable.food1, 1),
        CartItemData("Burger", "$2", R.drawable.food2, 2),
        CartItemData("Pasta", "$6", R.drawable.food3, 1)
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "Your Cart",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
        ) {
            items(cartItems) { item ->
                CartItemRow(item, 
                    onIncrease = { 
                        val index = cartItems.indexOf(item)
                        if (index != -1) cartItems[index] = item.copy(quantity = item.quantity + 1)
                    },
                    onDecrease = { 
                        if (item.quantity > 1) {
                            val index = cartItems.indexOf(item)
                            if (index != -1) cartItems[index] = item.copy(quantity = item.quantity - 1)
                        }
                    },
                    onDelete = { cartItems.remove(item) }
                )
            }
        }
        
        Button(
            onClick = { /* Proceed to Checkout */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StartColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Proceed to Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CartItemRow(item: CartItemData, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Card(
                shape = RoundedCornerShape(12.dp),
                 modifier = Modifier.size(80.dp)
            ) {
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color=Color.Black)
                Text(item.price, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color=Color.Black)
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp).background(StartColor, RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(painterResource(R.drawable.minus), null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(painterResource(R.drawable.plus), null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(painterResource(R.drawable.trash), null, tint = Color.Gray)
            }
        }
    }
}

data class CartItemData(val name: String, val price: String, val image: Int, val quantity: Int)
