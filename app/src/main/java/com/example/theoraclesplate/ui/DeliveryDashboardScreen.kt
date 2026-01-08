package com.example.theoraclesplate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun DeliveryDashboardScreen(navController: NavController, viewModel: DeliveryDashboardViewModel = hiltViewModel()) {
    val auth = Firebase.auth
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ready for Pickup", "Out for Delivery", "Delivered")
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Delivery Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Logout",
                color = StartColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    auth.signOut()
                    navController.navigate("start") {
                        popUpTo("delivery_dashboard") { inclusive = true }
                    }
                }
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = StartColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = StartColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    selectedContentColor = StartColor,
                    unselectedContentColor = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        when (selectedTab) {
            0 -> OrdersListView(orders = state.readyForPickupOrders, onUpdate = { order ->
                // TODO: Handle order status update
            })
            1 -> OrdersListView(orders = state.outForDeliveryOrders, onUpdate = { order ->
                // TODO: Handle order status update
            })
            2 -> OrdersListView(orders = state.deliveredOrders, onUpdate = null)
        }
    }
}

@Composable
fun OrdersListView(orders: List<Order>, onUpdate: ((Order) -> Unit)?) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(orders) { order ->
            DeliveryOrderCard(order = order, onUpdate = onUpdate)
        }
    }
}

@Composable
fun DeliveryOrderCard(order: Order, onUpdate: ((Order) -> Unit)?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("User: ${order.userName}", color = Color.White.copy(alpha = 0.7f))
            Text("Address: ${order.address}", color = Color.White.copy(alpha = 0.7f))
            Text("Total: ${order.totalAmount}", color = Color.White.copy(alpha = 0.7f))
            Text(
                "Status: ${order.status}", color = when (order.status) {
                    "Ready" -> Color.Yellow
                    "Out for Delivery" -> Color.Cyan
                    "Delivered" -> Color.Green
                    else -> Color.White
                }
            )

            onUpdate?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { it(order) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (order.status == "Ready") StartColor else Color(0xFF00C851))
                    ) {
                        Text(if (order.status == "Ready") "Pick Up Order" else "Mark as Delivered")
                    }
                }
            }
        }
    }
}
