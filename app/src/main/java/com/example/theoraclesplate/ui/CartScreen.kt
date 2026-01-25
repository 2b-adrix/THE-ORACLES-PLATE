package com.example.theoraclesplate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.CartItem
import com.example.theoraclesplate.ui.cart.CartEvent
import com.example.theoraclesplate.ui.cart.CartViewModel
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun CartScreen(rootNavController: NavController, viewModel: CartViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Text(
            text = "Your Cart",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        if (state.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else if (state.cartItems.isEmpty()) {
             Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.White.copy(alpha = 0.7f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                items(state.cartItems) { cartItem ->
                    CartItemRow(
                        item = cartItem, 
                        onIncrease = { 
                            viewModel.onEvent(CartEvent.UpdateQuantity(cartItem.id, cartItem.quantity + 1))
                        },
                        onDecrease = { 
                            if (cartItem.quantity > 1) {
                                viewModel.onEvent(CartEvent.UpdateQuantity(cartItem.id, cartItem.quantity - 1))
                            } else {
                                viewModel.onEvent(CartEvent.RemoveFromCart(cartItem.id))
                            }
                        },
                        onDelete = { 
                            viewModel.onEvent(CartEvent.RemoveFromCart(cartItem.id))
                        }
                    )
                }
            }
            
            Button(
                onClick = { rootNavController.navigate("checkout_screen") },
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
}

@Composable
fun CartItemRow(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image.ifEmpty { R.drawable.food1 },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("$${item.price}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.8f))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(StartColor, RoundedCornerShape(8.dp))
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
                Icon(painterResource(R.drawable.trash), null, tint = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}
