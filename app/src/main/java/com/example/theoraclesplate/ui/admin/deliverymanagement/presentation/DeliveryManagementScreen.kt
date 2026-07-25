package com.example.theoraclesplate.ui.admin.deliverymanagement.presentation

import androidx.compose.foundation.layout.* 
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.model.User
import com.example.theoraclesplate.ui.admin.deliverymanagement.viewmodel.DeliveryManagementState
import com.example.theoraclesplate.ui.admin.deliverymanagement.viewmodel.DeliveryManagementViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun DeliveryManagementScreen(
    viewModel: DeliveryManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    DeliveryManagementScreenContent(state = state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryManagementScreenContent(state: DeliveryManagementState) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Delivery Management", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.deliveryUsers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No delivery partners found.", color = Color.White)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.deliveryUsers) { user ->
                            DeliveryPartnerItem(user = user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryPartnerItem(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name.ifEmpty { "Unknown" }, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = user.email, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
            }
            // Add status or actions here if needed
            Surface(
                color = Color.Green.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "ACTIVE",
                    color = Color.Green,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun DeliveryManagementScreenPreview() {
    THEORACLESPLATETheme {
        DeliveryManagementScreenContent(
            state = DeliveryManagementState(
                deliveryUsers = listOf(
                    User(uid = "1", name = "Fast Delivery", email = "fast@delivery.com"),
                    User(uid = "2", name = "Quick Rider", email = "quick@rider.com")
                ),
                isLoading = false
            )
        )
    }
}
