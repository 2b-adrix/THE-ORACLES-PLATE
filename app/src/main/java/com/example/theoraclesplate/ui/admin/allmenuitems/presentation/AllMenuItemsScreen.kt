package com.example.theoraclesplate.ui.admin.allmenuitems.presentation

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
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.admin.allmenuitems.viewmodel.AllMenuItemsState
import com.example.theoraclesplate.ui.admin.allmenuitems.viewmodel.AllMenuItemsViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun AllMenuItemsScreen(
    viewModel: AllMenuItemsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    AllMenuItemsScreenContent(
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMenuItemsScreenContent(
    state: AllMenuItemsState
) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("All Menu Items", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.menuItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No menu items found.", color = Color.White)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.menuItems) { (sellerId, menuItem) ->
                            AdminMenuItemCard(sellerId = sellerId, menuItem = menuItem)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMenuItemCard(sellerId: String, menuItem: FoodItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = menuItem.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "Price: $${menuItem.price}", color = MaterialTheme.colorScheme.primary)
            Text(text = "Seller ID: $sellerId", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview
@Composable
fun AllMenuItemsScreenPreview() {
    THEORACLESPLATETheme {
        AllMenuItemsScreenContent(
            state = AllMenuItemsState(
                menuItems = listOf(
                    "seller1" to FoodItem(name = "Classic Burger", price = 12.99),
                    "seller2" to FoodItem(name = "Pepperoni Pizza", price = 15.50)
                ),
                isLoading = false
            )
        )
    }
}
