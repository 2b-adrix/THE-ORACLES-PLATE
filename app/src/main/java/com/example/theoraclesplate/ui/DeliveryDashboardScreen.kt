package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardEvent
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDashboardScreen(
    navController: NavController,
    viewModel: DeliveryDashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Ready for Pickup", "Out for Delivery", "Delivered")
    var isListVisible by remember { mutableStateOf(true) }

    Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DeliveryDashboardViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery Dashboard") },
                actions = {
                    IconButton(onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("start") {
                            popUpTo("delivery_dashboard") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setBuiltInZoomControls(true)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        if (state.orderLocations.isNotEmpty()) {
                            controller.setCenter(state.orderLocations.first())
                        }
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    state.orderLocations.forEach {
                        val marker = Marker(mapView)
                        marker.position = it
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate() // Redraw the map
                }
            )
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
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { isListVisible = !isListVisible }
                ) {
                    Icon(
                        imageVector = if (isListVisible) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = "Toggle List",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                AnimatedVisibility(visible = isListVisible) {
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
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun OrderList(orders: List<Order>, actionText: String?, onActionClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp).height(200.dp)) {
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
