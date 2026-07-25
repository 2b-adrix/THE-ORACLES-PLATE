package com.example.theoraclesplate.ui.admin.analytics.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.ui.admin.analytics.viewmodel.AnalyticsState
import com.example.theoraclesplate.ui.admin.analytics.viewmodel.AnalyticsViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.common.SimpleBarChart
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    AnalyticsScreenContent(state = state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreenContent(state: AnalyticsState) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Analytics", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error, color = Color.Red)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            AnalyticsCard("Total Users", state.analyticsData["totalUsers"].toString())
                            Spacer(modifier = Modifier.height(16.dp))
                            AnalyticsCard("Total Sellers", state.analyticsData["totalSellers"].toString())
                            Spacer(modifier = Modifier.height(16.dp))
                            AnalyticsCard("Total Orders", state.analyticsData["totalOrders"].toString())
                            Spacer(modifier = Modifier.height(16.dp))
                            AnalyticsCard("Total Revenue", "$${state.analyticsData["totalRevenue"]}")
                            Spacer(modifier = Modifier.height(16.dp))
                            AnalyticsCard("Pending Sellers", state.analyticsData["pendingSellers"].toString())
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Users vs Sellers", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    SimpleBarChart(
                                        data = mapOf(
                                            "Users" to (state.analyticsData["totalUsers"] as? Int ?: 0),
                                            "Sellers" to (state.analyticsData["totalSellers"] as? Int ?: 0)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview
@Composable
fun AnalyticsScreenPreview() {
    THEORACLESPLATETheme {
        AnalyticsScreenContent(
            state = AnalyticsState(
                analyticsData = mapOf(
                    "totalUsers" to 150,
                    "totalSellers" to 25,
                    "totalOrders" to 450,
                    "totalRevenue" to 12500.0,
                    "pendingSellers" to 5
                ),
                isLoading = false
            )
        )
    }
}
