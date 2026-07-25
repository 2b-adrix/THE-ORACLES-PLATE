package com.example.theoraclesplate.ui.checkout.presentation

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.OrderItem
import com.example.theoraclesplate.ui.checkout.viewmodel.CheckoutEvent
import com.example.theoraclesplate.ui.checkout.viewmodel.CheckoutState
import com.example.theoraclesplate.ui.checkout.viewmodel.CheckoutViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppButton
import com.example.theoraclesplate.ui.components.AppCard
import com.example.theoraclesplate.ui.components.CardStyle
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CheckoutScreen(navController: NavController, viewModel: CheckoutViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CheckoutViewModel.UiEvent.OrderPlaced -> {
                    Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
                is CheckoutViewModel.UiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is CheckoutViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CheckoutScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenContent(
    state: CheckoutState,
    onEvent: (CheckoutEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(500))
    }

    PremiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Checkout", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .alpha(alpha.value)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Delivery Address", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.address,
                    onValueChange = { onEvent(CheckoutEvent.AddressChanged(it)) },
                    label = { Text("Street Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Payment Method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                PaymentOption(
                    title = "Cash on Delivery",
                    selected = state.paymentMethod == "COD",
                    onClick = { onEvent(CheckoutEvent.PaymentMethodChanged("COD")) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Order Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                AppCard(
                    style = CardStyle.Glass,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        state.cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.quantity} x ${item.name}", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                                Text("$${String.format("%.2f", item.price * item.quantity)}", fontWeight = FontWeight.SemiBold, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("$${String.format("%.2f", state.totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                AppButton(
                    text = "Place Order",
                    onClick = { onEvent(CheckoutEvent.PlaceOrder) },
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun PaymentOption(title: String, selected: Boolean, onClick: () -> Unit) {
    AppCard(
        style = CardStyle.Glass,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary, unselectedColor = Color.White.copy(alpha = 0.5f))
            )
        }
    }
}

@Preview
@Composable
fun CheckoutScreenPreview() {
    THEORACLESPLATETheme {
        CheckoutScreenContent(
            state = CheckoutState(
                address = "123 Magic Lane, Oracle City",
                cartItems = listOf(
                    OrderItem(id = "1", name = "Golden Pizza", price = 25.0, quantity = 1),
                    OrderItem(id = "2", name = "Mystic Soda", price = 5.0, quantity = 2)
                ),
                totalAmount = 35.0,
                isLoading = false
            ),
            onEvent = {},
            onBackClick = {}
        )
    }
}
