package com.example.theoraclesplate.ui.seller.orders.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.theoraclesplate.model.Order

@Composable
fun OrderItemCard(order: Order, onUpdateStatus: (String, String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Order ID: ${order.orderId}")
            Text(text = "Customer: ${order.userName}")
            Text(text = "Total: $${order.totalAmount}")
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach {
                Text(text = "- ${it.quantity} x ${it.name}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = { onUpdateStatus(order.orderId, "Preparing") }) {
                    Text(text = "Preparing")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onUpdateStatus(order.orderId, "Ready") }) {
                    Text(text = "Ready")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onUpdateStatus(order.orderId, "Out for Delivery") }) {
                    Text(text = "Out for Delivery")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onUpdateStatus(order.orderId, "Delivered") }) {
                    Text(text = "Delivered")
                }
            }
        }
    }
}
