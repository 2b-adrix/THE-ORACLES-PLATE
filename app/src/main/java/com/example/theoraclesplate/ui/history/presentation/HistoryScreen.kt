package com.example.theoraclesplate.ui.history.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.OrderItem
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppButton
import com.example.theoraclesplate.ui.components.AppCard
import com.example.theoraclesplate.ui.components.CardStyle
import com.example.theoraclesplate.ui.history.viewmodel.HistoryEvent
import com.example.theoraclesplate.ui.history.viewmodel.HistoryState
import com.example.theoraclesplate.ui.history.viewmodel.HistoryViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when(it) {
                is HistoryViewModel.UiEvent.NavigateToCart -> {
                    navController.navigate("cart_screen")
                }
                is HistoryViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    HistoryScreenContent(
        state = historyState,
        onBack = { navController.popBackStack() },
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    state: HistoryState,
    onBack: () -> Unit,
    onEvent: (HistoryEvent) -> Unit
) {
    var showCancelDialog by remember { mutableStateOf<String?>(null) }

    if (showCancelDialog != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Cancel Order") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(HistoryEvent.CancelOrder(showCancelDialog!!))
                        showCancelDialog = null
                    }
                ) {
                    Text("Yes, Cancel", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) {
                    Text("No", color = Color.White)
                }
            },
            containerColor = Color(0xFF1A1A2E),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f)
        )
    }

    PremiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Order History", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
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
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error, color = Color.Red)
                    }
                } else if (state.orders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "No History",
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(120.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No order history", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        itemsIndexed(state.orders) { index, order ->
                            val alpha = remember { Animatable(0f) }
                            LaunchedEffect(key1 = order) {
                                delay(index * 80L)
                                alpha.animateTo(1f, tween(300))
                            }
                            HistoryItemRow(
                                order,
                                modifier = Modifier.alpha(alpha.value),
                                onReorder = { onEvent(HistoryEvent.Reorder(order)) },
                                onCancel = { showCancelDialog = order.orderId }
                            )
                        }
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
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(order.timestamp))
    }

    AppCard(
        style = CardStyle.Glass,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = if (order.items.isNotEmpty() && order.items.first().imageUrl.isNotEmpty()) order.items.first().imageUrl else R.drawable.logo,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(order.items.firstOrNull()?.name ?: "Premium Order", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(String.format("$%.2f", order.totalAmount), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text(formattedDate, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                }

                Surface(
                    color = when (order.status) {
                        "Pending" -> Color(0xFF33B5E5).copy(alpha = 0.2f)
                        "Cancelled" -> Color(0xFFFF4444).copy(alpha = 0.2f)
                        "Delivered" -> Color(0xFF00C851).copy(alpha = 0.2f)
                        else -> Color.White.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "Pending" -> Color(0xFF33B5E5)
                            "Cancelled" -> Color(0xFFFF4444)
                            "Delivered" -> Color(0xFF00C851)
                            else -> Color.White
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            if (expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(Modifier.height(12.dp))
                    order.items.forEach {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${it.name} x ${it.quantity}", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                            Text("$${String.format("%.2f", it.price * it.quantity)}", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        if (order.status == "Pending") {
                            TextButton(onClick = onCancel) {
                                Text("Cancel Order", color = Color.Red.copy(alpha = 0.8f))
                            }
                        } else if (order.status != "Cancelled") {
                            AppButton(
                                text = "Reorder",
                                onClick = onReorder,
                                modifier = Modifier.height(36.dp)
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
fun HistoryScreenPreview() {
    THEORACLESPLATETheme {
        HistoryScreenContent(
            state = HistoryState(
                orders = listOf(
                    Order(
                        orderId = "1", 
                        totalAmount = 45.0, 
                        status = "Delivered", 
                        timestamp = System.currentTimeMillis(),
                        items = listOf(OrderItem(id = "1", name = "Golden Pasta", price = 22.5, quantity = 2))
                    ),
                    Order(
                        orderId = "2", 
                        totalAmount = 15.0, 
                        status = "Pending", 
                        timestamp = System.currentTimeMillis() - 86400000,
                        items = listOf(OrderItem(id = "2", name = "Magic Drink", price = 7.5, quantity = 2))
                    )
                ),
                isLoading = false
            ),
            onBack = {},
            onEvent = {}
        )
    }
}
