package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun SellerDashboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Orders", "Menu")
    val context = LocalContext.current
    val auth = Firebase.auth
    
    // Debug: Check if user is actually logged in
    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
             Toast.makeText(context, "DEBUG: User is NOT logged in!", Toast.LENGTH_LONG).show()
        } else {
             Toast.makeText(context, "DEBUG: Logged in as ${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Seller Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Logout",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("seller_dashboard") { inclusive = true }
                    }
                }
            )
        }
        
        // Debug Connection Button
        Button(
            onClick = {
                val testRef = Firebase.database.reference.child("test_connection")
                testRef.setValue("Connected at ${System.currentTimeMillis()}")
                    .addOnSuccessListener { 
                        Toast.makeText(context, "Connection Success! Rules are good.", Toast.LENGTH_SHORT).show() 
                    }
                    .addOnFailureListener { e -> 
                        Toast.makeText(context, "Connection FAILED: ${e.message}", Toast.LENGTH_LONG).show() 
                    }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Test Database Connection")
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.White, contentColor = StartColor) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> SellerOrdersView()
            1 -> SellerMenuView()
        }
    }
}

@Composable
fun SellerOrdersView() {
    val database = Firebase.database
    val orders = remember { mutableStateListOf<Order>() }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val ordersRef = database.reference.child("orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }
                orders.sortByDescending { it.timestamp }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                Toast.makeText(context, "Error fetching orders: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = StartColor)
        }
    } else if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders yet", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(orders) { order ->
                SellerOrderItem(order)
            }
        }
    }
}

@Composable
fun SellerOrderItem(order: Order) {
    val database = Firebase.database
    
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold)
                Text(order.totalAmount, fontWeight = FontWeight.Bold, color = StartColor)
            }
            Text("User: ${order.userName}", fontSize = 14.sp, color = Color.Gray)
            if (order.address.isNotEmpty()) {
                 Text("Addr: ${order.address}", fontSize = 12.sp, color = Color.Gray, lineHeight = 14.sp)
            }
            Text("Status: ${order.status}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, 
                color = when (order.status) {
                    "Completed" -> Color.Green
                    "Cancelled" -> Color.Red
                    else -> Color.Blue
                })
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            order.items.forEach { item ->
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("${item.quantity} x ${item.name}")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                 if (order.status == "Pending") {
                     Button(
                        onClick = {
                             database.reference.child("orders").child(order.orderId).child("status").setValue("Preparing")
                             database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).child("status").setValue("Preparing")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StartColor)
                    ) {
                        Text("Accept & Prepare")
                    }
                 } else if (order.status == "Preparing") {
                     Button(
                        onClick = {
                             database.reference.child("orders").child(order.orderId).child("status").setValue("Ready")
                             database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).child("status").setValue("Ready")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green
                    ) {
                        Text("Mark Ready")
                    }
                 } else if (order.status == "Ready") {
                     Button(
                        onClick = {
                             database.reference.child("orders").child(order.orderId).child("status").setValue("Completed")
                             database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).child("status").setValue("Completed")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green
                    ) {
                        Text("Mark Completed")
                    }
                 } else {
                     Text("Status: ${order.status}", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                 }
            }
        }
    }
}

@Composable
fun SellerMenuView() {
    val database = Firebase.database
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Pair<String, FoodItem>?>(null) }
    
    // Add/Edit Item State
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
                        val newItem = FoodItem(itemName, itemPrice, itemImage, itemDesc)
                        database.reference.child("menu").push().setValue(newItem)
                        Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show()
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
                        val updatedItem = FoodItem(itemName, itemPrice, itemImage, itemDesc)
                        editingItem?.let { (key, _) ->
                             database.reference.child("menu").child(key).setValue(updatedItem)
                        }
                        Toast.makeText(context, "Item Updated!", Toast.LENGTH_SHORT).show()
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
        // Pair of Key -> Item
        val menuItems = remember { mutableStateListOf<Pair<String, FoodItem>>() }
        
        LaunchedEffect(Unit) {
            val menuRef = database.reference.child("menu")
            menuRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    menuItems.clear()
                    for (child in snapshot.children) {
                        val item = child.getValue(FoodItem::class.java)
                        if (item != null) {
                            menuItems.add(Pair(child.key ?: "", item))
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error fetching menu: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
        
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(menuItems) { (key, item) ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.name, fontWeight = FontWeight.Bold)
                            Text(item.price, color = StartColor)
                        }
                        
                        Row {
                             IconButton(onClick = {
                                editingItem = key to item
                                itemName = item.name
                                itemPrice = item.price
                                itemDesc = item.description
                                itemImage = item.image
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                            }
                            
                            IconButton(onClick = {
                                database.reference.child("menu").child(key).removeValue()
                                Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
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
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = StartColor
        ) {
            Text("+", fontSize = 24.sp, color = Color.White)
        }
    }
}
