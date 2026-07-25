package com.example.theoraclesplate.ui.seller.orders.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.ui.seller.viewmodel.SellerOrdersEvent
import com.example.theoraclesplate.ui.seller.viewmodel.SellerOrdersViewModel

@Composable
fun SellerOrdersScreen(viewModel: SellerOrdersViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Pending", "Preparing", "Ready", "Out for Delivery", "Delivered")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        val filteredOrders = state.orders.filter { it.status == tabs[selectedTabIndex] }

        OrderList(orders = filteredOrders, onUpdateStatus = { orderId, newStatus ->
            viewModel.onEvent(SellerOrdersEvent.UpdateOrderStatus(orderId, newStatus))
        })
    }
}
