package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyState by viewModel.historyState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchOrderHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = StartColor)
            }
            Text(
                text = "Order History",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        when (val state = historyState) {
            is HistoryState.Loading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StartColor)
                }
            }
            is HistoryState.Success -> {
                if (state.items.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No order history", color = Color.White.copy(alpha = 0.7f))
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                        items(state.items) { item ->
                            HistoryItemRow(
                                item,
                                onReorder = { navController.navigate("home") },
                                onCancel = { viewModel.cancelOrder(item.orderId) }
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

@Composable
fun HistoryItemRow(item: HistoryItem, onReorder: () -> Unit, onCancel: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (item.image.isNotEmpty()) item.image else R.drawable.food1,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp))
            )

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(item.price, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = StartColor)
                Text(item.date, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                Text(
                    "Status: ${item.status}", fontSize = 12.sp, fontWeight = FontWeight.Bold,
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
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Cancel", fontSize = 12.sp, color = Color.White)
                }
            } else {
                Button(
                    onClick = onReorder,
                    colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Reorder", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}
