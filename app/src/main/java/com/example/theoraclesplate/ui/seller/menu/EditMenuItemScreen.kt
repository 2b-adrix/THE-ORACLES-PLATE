package com.example.theoraclesplate.ui.seller.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.FoodItem

@Composable
fun EditMenuItemScreen(
    navController: NavController,
    viewModel: SellerMenuViewModel = hiltViewModel(),
    menuItemId: String
) {
    val state = viewModel.state.value
    val menuItem = state.menuItems.find { it.first == menuItemId }?.second

    if (menuItem == null) {
        // Handle error: menu item not found
        // You might want to show a toast or navigate back
        return
    }

    var name by remember { mutableStateOf(menuItem.name) }
    var description by remember { mutableStateOf(menuItem.description) }
    var price by remember { mutableStateOf(menuItem.price.toString()) }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            Text("Edit Menu Item", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val updatedItem = menuItem.copy(
                    name = name,
                    description = description,
                    price = price.toDoubleOrNull() ?: 0.0
                )
                viewModel.onEvent(SellerMenuEvent.EditMenuItem(menuItemId, updatedItem))
                navController.popBackStack()
            }) {
                Text("Save Changes")
            }
        }
    }
}
