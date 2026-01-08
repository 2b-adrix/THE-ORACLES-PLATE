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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.seller.menu.SellerMenuEvent
import com.example.theoraclesplate.ui.seller.menu.SellerMenuViewModel
import com.example.theoraclesplate.ui.seller.orders.SellerOrdersEvent
import com.example.theoraclesplate.ui.seller.orders.SellerOrdersViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF1A1A2E))) { // Themed for dark mode

        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Seller Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Logout",
                color = StartColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    auth.signOut()
                    try {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInClient.signOut()
                    } catch (_: Exception) {
                        // Ignore
                    }
                    navController.navigate("login") {
                        popUpTo("seller_dashboard") { inclusive = true }
                    }
                }
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = StartColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = StartColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    selectedContentColor = StartColor,
                    unselectedContentColor = Color.White.copy(alpha = 0.7f)
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
fun SellerOrdersView(viewModel: SellerOrdersViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = StartColor)
        }
    } else if (state.orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders yet", color = Color.White.copy(alpha = 0.7f))
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(state.orders) { order ->
                SellerOrderItem(order, onUpdateStatus = {
                    viewModel.onEvent(SellerOrdersEvent.UpdateOrderStatus(order.orderId, it))
                })
            }
        }
    }
}

@Composable
fun SellerOrderItem(order: Order, onUpdateStatus: (String) -> Unit) {

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)) // Glassmorphism
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, color = Color.White)
                Text(order.totalAmount, fontWeight = FontWeight.Bold, color = StartColor)
            }
            Text("User: ${order.userName}", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
            if (order.address.isNotEmpty()) {
                 Text("Addr: ${order.address}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), lineHeight = 14.sp)
            }
            Text("Status: ${order.status}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                color = when (order.status) {
                    "Completed" -> Color(0xFF00C851)
                    "Cancelled" -> Color(0xFFFF4444)
                    else -> Color(0xFF33B5E5)
                })

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.2f))

            order.items.forEach { item ->
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("${item.quantity} x ${item.name}", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                when (order.status) {
                    "Pending" -> {
                        Button(
                           onClick = { onUpdateStatus("Preparing") },
                           colors = ButtonDefaults.buttonColors(containerColor = StartColor)
                       ) {
                           Text("Accept & Prepare")
                       }
                    }
                    "Preparing" -> {
                        Button(
                           onClick = { onUpdateStatus("Ready") },
                           colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C851)) // Green
                       ) {
                           Text("Mark Ready")
                       }
                    }
                    "Ready" -> {
                        Button(
                           onClick = { onUpdateStatus("Completed") },
                           colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C851)) // Green
                       ) {
                           Text("Mark Completed")
                       }
                    }
                    else -> {
                        Text("Status: ${order.status}", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
fun SellerMenuView(viewModel: SellerMenuViewModel = hiltViewModel()) {
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
                        viewModel.onEvent(SellerMenuEvent.AddItem(FoodItem(itemName, itemPrice, itemImage, itemDesc)))
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
                            viewModel.onEvent(SellerMenuEvent.UpdateItem(it.first, FoodItem(itemName, itemPrice, itemImage, itemDesc)))
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
                    shape = RoundedCornerShape(12.dp),
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
                             viewModel.onEvent(SellerMenuEvent.DeleteItem(key))
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
            Icon(Icons.Default.Edit, contentDescription = "Add Item")
        }
    }
}
