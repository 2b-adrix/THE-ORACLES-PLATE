package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardEvent
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDashboardScreen(
    navController: NavController,
    viewModel: DeliveryDashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ready for Pickup", "Out for Delivery", "Delivered")

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DeliveryDashboardViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            Column(modifier = Modifier.fillMaxSize()) {
                PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = title) }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> OrderList(orders = state.readyForPickupOrders, actionText = "Accept Order") {
                        viewModel.onEvent(DeliveryDashboardEvent.UpdateOrderStatus(it, "Out for Delivery"))
                    }
                    1 -> OrderList(orders = state.outForDeliveryOrders, actionText = "Mark as Delivered") {
                        viewModel.onEvent(DeliveryDashboardEvent.UpdateOrderStatus(it, "Delivered"))
                    }
                    2 -> OrderList(orders = state.deliveredOrders, actionText = null) {}
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun OrderList(orders: List<Order>, actionText: String?, onActionClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(orders) { order ->
            OrderCard(order = order, actionText = actionText, onActionClick = onActionClick)
        }
    }
}

@Composable
fun OrderCard(order: Order, actionText: String?, onActionClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Order ID: ${order.orderId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "User: ${order.userName}")
            Text(text = "Address: ${order.address}")
            Text(text = "Total: $${order.totalAmount}")
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach {
                Text(text = "- ${it.name} (x${it.quantity})")
            }
            if (actionText != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onActionClick(order.orderId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = actionText)
                }
            }
        }
    }
}
