package com.example.theoraclesplate.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.theme.StartColor
import com.example.theoraclesplate.ui.viewmodel.HistoryEvent
import com.example.theoraclesplate.ui.viewmodel.HistoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyState by viewModel.state.collectAsState()
    var showCancelDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when(it) {
                is HistoryViewModel.UiEvent.NavigateToCart -> {
                    navController.navigate("cart_screen")
                }
            }
        }
    }

    if (showCancelDialog != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Cancel Order") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(HistoryEvent.CancelOrder(showCancelDialog!!))
                        showCancelDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444))
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Transparent)
        ) {
            if (historyState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StartColor)
                }
            } else if (historyState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = historyState.error!!, color = Color.Red)
                }
            } else if (historyState.orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "No History",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No order history", color = Color.White.copy(alpha = 0.7f), fontSize = 20.sp)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    itemsIndexed(historyState.orders) { index, order ->
                        val alpha = remember { Animatable(0f) }
                        LaunchedEffect(key1 = order) {
                            delay(index * 100L)
                            alpha.animateTo(1f, tween(500))
                        }
                        HistoryItemRow(
                            order,
                            modifier = Modifier.alpha(alpha.value),
                            onReorder = { viewModel.onEvent(HistoryEvent.Reorder(order)) },
                            onCancel = { showCancelDialog = order.orderId }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(order: Order, modifier: Modifier = Modifier, onReorder: () -> Unit, onCancel: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val formattedDate = remember(order.timestamp) {
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
        sdf.format(Date(order.timestamp))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = if (order.items.isNotEmpty() && order.items.first().imageUrl.isNotEmpty()) order.items.first().imageUrl else R.drawable.logo,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(order.items.firstOrNull()?.name ?: "Unknown Item", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(String.format("$%.2f", order.totalAmount), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = StartColor)
                    Text(formattedDate, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(
                        "Status: ${order.status}", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "Pending" -> Color(0xFF33B5E5)
                            "Cancelled" -> Color(0xFFFF4444)
                            "Delivered" -> Color(0xFF00C851)
                            else -> Color.White
                        }
                    )
                }

                if (order.status == "Pending") {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = Color.White)
                    }
                } else if (order.status != "Cancelled") {
                    Button(
                        onClick = onReorder,
                        colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Reorder", fontSize = 14.sp, color = Color.Black)
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    order.items.forEach {
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("• ${it.name} (x${it.quantity})", color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}
