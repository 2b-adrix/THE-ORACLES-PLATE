package com.example.theoraclesplate.ui.admin.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnalyticsView(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val analyticsData = state.analyticsData

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AnalyticsCard(title = "Total Revenue", value = "$${String.format("%.2f", analyticsData.totalRevenue)}", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            AnalyticsCard(title = "Total Orders", value = analyticsData.totalOrders.toString(), modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            AnalyticsCard(title = "Total Users", value = analyticsData.totalUsers.toString(), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
