package com.example.theoraclesplate.ui.delivery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.seller.orders.OrderList

@Composable
fun DeliveryDashboardScreen(viewModel: DeliveryDashboardViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            Button(onClick = { selectedTab = 0 }) {
                Text(text = "Ready for Pickup")
            }
            Button(onClick = { selectedTab = 1 }) {
                Text(text = "Out for Delivery")
            }
        }

        val orders = if (selectedTab == 0) {
            state.readyForPickupOrders
        } else {
            state.outForDeliveryOrders
        }

        OrderList(orders = orders, onUpdateStatus = { orderId, newStatus ->
            // This is not ideal, but we'll use it for now.
            // We should create a separate event for accepting an order.
            if (newStatus == "Out for Delivery") {
                val order = state.readyForPickupOrders.find { it.orderId == orderId }
                if (order != null) {
                    viewModel.onEvent(DeliveryDashboardEvent.AcceptOrder(order))
                }
            } else {
                viewModel.onEvent(DeliveryDashboardEvent.UpdateOrderStatus(orderId, newStatus))
            }
        })
    }
}
