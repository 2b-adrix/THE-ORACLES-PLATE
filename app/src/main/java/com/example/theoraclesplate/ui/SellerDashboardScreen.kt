package com.example.theoraclesplate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.ui.seller.menu.SellerMenuEvent
import com.example.theoraclesplate.ui.seller.menu.SellerMenuViewModel

@Composable
fun SellerDashboardScreen(
    navController: NavController,
    viewModel: SellerMenuViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_menu_item") },
            ) {
                Icon(Icons.Filled.Add, "Add new menu item")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("My Menu", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.menuItems) { (menuItemId, menuItem) ->
                        MenuItem(name = menuItem.name, price = "$${menuItem.price}", onDelete = {
                            viewModel.onEvent(SellerMenuEvent.DeleteMenuItem(menuItemId))
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItem(name: String, price: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = name, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Text(text = price, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete menu item")
        }
    }
}
