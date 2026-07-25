package com.example.theoraclesplate.ui.admin.allorders.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.admin.allorders.viewmodel.AllOrdersEvent
import com.example.theoraclesplate.ui.admin.allorders.viewmodel.AllOrdersState
import com.example.theoraclesplate.ui.admin.allorders.viewmodel.AllOrdersViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun AllOrdersScreen(viewModel: AllOrdersViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    AllOrdersScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllOrdersScreenContent(
    state: AllOrdersState,
    onEvent: (AllOrdersEvent) -> Unit
) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("All Orders", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error, color = Color.Red)
                    }
                } else if (state.orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No orders found.", color = Color.White)
                    }
                } else {
                    LazyColumn {
                        items(state.orders) { order ->
                            AdminOrderItemCard(
                                order = order, 
                                onDelete = { onEvent(AllOrdersEvent.DeleteOrder(order.orderId)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AllOrdersScreenPreview() {
    THEORACLESPLATETheme {
        AllOrdersScreenContent(
            state = AllOrdersState(
                orders = listOf(
                    Order(orderId = "1", totalAmount = 150.0, status = "delivered"),
                    Order(orderId = "2", totalAmount = 75.0, status = "pending")
                ),
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
