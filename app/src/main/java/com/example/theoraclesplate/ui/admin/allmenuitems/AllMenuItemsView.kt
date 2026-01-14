package com.example.theoraclesplate.ui.admin.allmenuitems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun AllMenuItemsView(viewModel: AllMenuItemsViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Pair<String, FoodItem>?>(null) }

    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemDesc by remember { mutableStateOf("") }
    var itemImage by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Menu Item") },
            text = {
                Column {
                    OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Food Name") })
                    OutlinedTextField(value = itemPrice, onValueChange = { itemPrice = it }, label = { Text("Price (e.g. $10)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = itemDesc, onValueChange = { itemDesc = it }, label = { Text("Description") })
                    OutlinedTextField(value = itemImage, onValueChange = { itemImage = it }, label = { Text("Image URL") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (itemName.isNotEmpty() && itemPrice.isNotEmpty()) {
                        // The sellerId will be added in the ViewModel
                        viewModel.onEvent(AllMenuItemsEvent.AddMenuItem(FoodItem(itemName, itemPrice, itemImage, itemDesc)))
                        showAddDialog = false
                        itemName = ""; itemPrice = ""; itemDesc = ""; itemImage = ""
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditDialog && editingItem != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Menu Item") },
            text = {
                Column {
                    OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Food Name") })
                    OutlinedTextField(value = itemPrice, onValueChange = { itemPrice = it }, label = { Text("Price (e.g. $10)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = itemDesc, onValueChange = { itemDesc = it }, label = { Text("Description") })
                    OutlinedTextField(value = itemImage, onValueChange = { itemImage = it }, label = { Text("Image URL") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (itemName.isNotEmpty() && itemPrice.isNotEmpty()) {
                        editingItem?.let {
                             viewModel.onEvent(AllMenuItemsEvent.UpdateMenuItem(it.first, FoodItem(itemName, itemPrice, itemImage, itemDesc, it.second.sellerId)))
                        }
                        showEditDialog = false
                        itemName = ""; itemPrice = ""; itemDesc = ""; itemImage = ""
                        editingItem = null
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    editingItem = null
                }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(state.menuItems) { (key, item) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(item.price, color = Color.White.copy(alpha = 0.8f))
                        }
                        IconButton(onClick = {
                            editingItem = Pair(key, item)
                            itemName = item.name
                            itemPrice = item.price
                            itemDesc = item.description
                            itemImage = item.image
                            showEditDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = StartColor)
                        }
                        IconButton(onClick = {
                             viewModel.onEvent(AllMenuItemsEvent.DeleteMenuItem(item.sellerId, key))
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = { 
                itemName = ""; itemPrice = ""; itemDesc = ""; itemImage = ""
                showAddDialog = true 
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = StartColor
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }
}
