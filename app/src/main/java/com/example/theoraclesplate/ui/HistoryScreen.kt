package com.example.theoraclesplate.ui

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor
import com.example.theoraclesplate.ui.viewmodel.HistoryItem
import com.example.theoraclesplate.ui.viewmodel.HistoryState
import com.example.theoraclesplate.ui.viewmodel.HistoryViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyState by viewModel.historyState.collectAsState()
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchOrderHistory()
    }

    if (showCancelDialog != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Cancel Order") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder(showCancelDialog!!)
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
            when (val state = historyState) {
                is HistoryState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = StartColor)
                    }
                }
                is HistoryState.Success -> {
                    if (state.items.isEmpty()) {
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
                            itemsIndexed(state.items) { index, item ->
                                val alpha = remember { Animatable(0f) }
                                LaunchedEffect(key1 = item) {
                                    delay(index * 100L)
                                    alpha.animateTo(1f, tween(500))
                                }
                                HistoryItemRow(
                                    item,
                                    modifier = Modifier.alpha(alpha.value),
                                    onReorder = { /*TODO*/ },
                                    onCancel = { showCancelDialog = item.orderId }
                                )
                            }
                        }
                    }
                }
                is HistoryState.Error -> {
                    Toast.makeText(context, "History Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItem, modifier: Modifier = Modifier, onReorder: () -> Unit, onCancel: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

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
                    model = if (item.items.isNotEmpty() && item.items.first().image.isNotEmpty()) item.items.first().image else R.drawable.logo,
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
                    Text(item.items.firstOrNull()?.name ?: "Unknown Item", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(item.price, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = StartColor)
                    Text(item.date, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(
                        "Status: ${item.status}", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = when (item.status) {
                            "Pending" -> Color(0xFF33B5E5)
                            "Cancelled" -> Color(0xFFFF4444)
                            "Completed" -> Color(0xFF00C851)
                            else -> Color.White
                        }
                    )
                }

                if (item.status == "Pending") {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = Color.White)
                    }
                } else if (item.status != "Cancelled") {
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
                    item.items.forEach {
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("• ${it.name} (x${it.quantity})", color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}
