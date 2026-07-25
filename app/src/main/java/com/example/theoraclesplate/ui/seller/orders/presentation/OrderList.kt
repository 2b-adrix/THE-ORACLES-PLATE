package com.example.theoraclesplate.ui.seller.orders.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.theoraclesplate.model.Order

@Composable
fun OrderList(orders: List<Order>, onUpdateStatus: (String, String) -> Unit) {
    LazyColumn {
        items(orders) { order ->
            OrderItemCard(order = order, onUpdateStatus = onUpdateStatus)
        }
    }
}
