package com.example.theoraclesplate.ui.cart.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.CartItem
import com.example.theoraclesplate.ui.cart.viewmodel.CartEvent
import com.example.theoraclesplate.ui.cart.viewmodel.CartState
import com.example.theoraclesplate.ui.cart.viewmodel.CartViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppButton
import com.example.theoraclesplate.ui.components.AppCard
import com.example.theoraclesplate.ui.components.CardStyle
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CartScreen(rootNavController: NavController, viewModel: CartViewModel = hiltViewModel()) {
    val state by viewModel.cartState.collectAsState()

    CartScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onCheckoutClick = { rootNavController.navigate("checkout_screen") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenContent(
    state: CartState,
    onEvent: (CartEvent) -> Unit,
    onCheckoutClick: () -> Unit
) {
    PremiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Your Cart", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(
                                imageVector = Icons.Default.RemoveShoppingCart,
                                contentDescription = "Empty Cart",
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(120.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Your cart is empty", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        itemsIndexed(state.cartItems) { index, cartItem ->
                            val alpha = remember { Animatable(0f) }
                            LaunchedEffect(key1 = cartItem) {
                                delay(index * 80L)
                                alpha.animateTo(1f, tween(300))
                            }
                            CartItemRow(
                                item = cartItem, 
                                modifier = Modifier.alpha(alpha.value),
                                onIncrease = { 
                                    onEvent(CartEvent.UpdateQuantity(cartItem.id, cartItem.quantity + 1))
                                },
                                onDecrease = { 
                                    if (cartItem.quantity > 1) {
                                        onEvent(CartEvent.UpdateQuantity(cartItem.id, cartItem.quantity - 1))
                                    } else {
                                        onEvent(CartEvent.RemoveFromCart(cartItem.id))
                                    }
                                },
                                onDelete = { 
                                    onEvent(CartEvent.RemoveFromCart(cartItem.id))
                                }
                            )
                        }
                    }
                    
                    AppCard(
                        style = CardStyle.Glass,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.7f))
                            Text(String.format("$%.2f", state.totalPrice), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        AppButton(
                            text = "Proceed to Checkout",
                            onClick = onCheckoutClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem, 
    modifier: Modifier = Modifier, 
    onIncrease: () -> Unit, 
    onDecrease: () -> Unit, 
    onDelete: () -> Unit
) {
    AppCard(
        style = CardStyle.Glass,
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl.ifEmpty { R.drawable.logo },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(String.format("$%.2f", item.price), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    IconButton(
                        onClick = onDecrease, 
                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                    ) {
                        Icon(painterResource(R.drawable.minus), null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    }
                    Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                    IconButton(
                        onClick = onIncrease, 
                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                    ) {
                        Icon(painterResource(R.drawable.plus), null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(painterResource(R.drawable.trash), null, tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Preview
@Composable
fun CartScreenPreview() {
    THEORACLESPLATETheme {
        CartScreenContent(
            state = CartState(
                cartItems = listOf(
                    CartItem(id = "1", name = "Classic Burger", price = 12.50, quantity = 2),
                    CartItem(id = "2", name = "French Fries", price = 5.00, quantity = 1)
                ),
                totalPrice = 30.00,
                isLoading = false
            ),
            onEvent = {},
            onCheckoutClick = {}
        )
    }
}
