package com.example.theoraclesplate.ui.admin.allmenuitems

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.theoraclesplate.model.FoodItem

@Composable
fun MenuItemCard(menuItem: FoodItem, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${menuItem.name}")
            Text(text = "Price: $${menuItem.price}")
            Text(text = "Seller ID: ${menuItem.sellerId}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDelete) {
                Text(text = "Delete")
            }
        }
    }
}
