package com.example.theoraclesplate.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(rootNavController: NavController, viewModel: CartViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Cart") }) },
        containerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Transparent)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StartColor)
                }
            } else if (state.cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(), 
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = "Empty Cart",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Your cart is empty", color = Color.White.copy(alpha = 0.7f), fontSize = 20.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(state.cartItems) { index, cartItem ->
                        val alpha = remember { Animatable(0f) }
                        LaunchedEffect(key1 = cartItem) {
                            delay(index * 100L)
                            alpha.animateTo(1f, tween(500))
                        }
                        CartItemRow(
                            item = cartItem, 
                            modifier = Modifier.alpha(alpha.value),
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
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("$${state.totalPrice}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StartColor)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { rootNavController.navigate("checkout_screen") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Proceed to Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, modifier: Modifier = Modifier, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image.ifEmpty { R.drawable.logo },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$${item.price}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = StartColor)
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp).background(StartColor, RoundedCornerShape(8.dp))) {
                        Icon(painterResource(R.drawable.minus), null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                    Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp).background(StartColor, RoundedCornerShape(8.dp))) {
                        Icon(painterResource(R.drawable.plus), null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(painterResource(R.drawable.trash), null, tint = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}
