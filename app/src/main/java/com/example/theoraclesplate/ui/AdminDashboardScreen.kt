package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
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
import com.example.theoraclesplate.model.User
import com.example.theoraclesplate.ui.admin.analytics.AnalyticsViewModel
import com.example.theoraclesplate.ui.admin.allmenuitems.AllMenuItemsEvent
import com.example.theoraclesplate.ui.admin.allmenuitems.AllMenuItemsViewModel
import com.example.theoraclesplate.ui.admin.allorders.AllOrdersEvent
import com.example.theoraclesplate.ui.admin.allorders.AllOrdersViewModel
import com.example.theoraclesplate.ui.admin.allusers.AllUsersEvent
import com.example.theoraclesplate.ui.admin.allusers.AllUsersViewModel
import com.example.theoraclesplate.ui.admin.deliverymanagement.DeliveryManagementEvent
import com.example.theoraclesplate.ui.admin.deliverymanagement.DeliveryManagementViewModel
import com.example.theoraclesplate.ui.admin.pendingsellers.PendingSellersEvent
import com.example.theoraclesplate.ui.admin.pendingsellers.PendingSellersViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun AdminDashboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Analytics", "Pending Sellers", "All Users", "All Orders", "All Menu Items", "Delivery Management", "Notifications")
    val auth = Firebase.auth

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF1A1A2E))) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Admin Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Logout",
                color = StartColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    auth.signOut()
                    navController.navigate("start") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }
            )
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = StartColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = StartColor
                )
            },
            edgePadding = 0.dp
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
            0 -> AnalyticsView()
            1 -> PendingSellersView()
            2 -> AllUsersView()
            3 -> AllOrdersView()
            4 -> AllMenuItemsView()
            5 -> DeliveryManagementView()
            6 -> NotificationsView()
        }
    }
}

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

@Composable
fun PendingSellersView(viewModel: PendingSellersViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(state.pendingSellers) { (key, user) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(user.email, color = Color.White.copy(alpha = 0.7f))
                    }
                    Button(
                        onClick = {
                            viewModel.onEvent(PendingSellersEvent.ApproveSeller(key))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StartColor)
                    ) {
                        Text("Approve")
                    }
                }
            }
        }
    }
}

@Composable
fun AllUsersView(viewModel: AllUsersViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(state.users) { (key, user) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(user.email, color = Color.White.copy(alpha = 0.7f))
                        Text("Role: ${user.role}", color = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = { 
                        viewModel.onEvent(AllUsersEvent.DeleteUser(key))
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun AllOrdersView(viewModel: AllOrdersViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(state.orders) { (key, order) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("User: ${order.userName}", color = Color.White.copy(alpha = 0.7f))
                        Text("Total: ${order.totalAmount}", color = Color.White.copy(alpha = 0.7f))
                        Text("Status: ${order.status}", color = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = { 
                        viewModel.onEvent(AllOrdersEvent.DeleteOrder(key))
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Order", tint = Color.Red)
                    }
                }
            }
        }
    }
}

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
                             viewModel.onEvent(AllMenuItemsEvent.DeleteMenuItem(key))
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

@Composable
fun DeliveryManagementView(viewModel: DeliveryManagementViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(state.deliveryUsers) { (key, user) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(user.email, color = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = { 
                        viewModel.onEvent(DeliveryManagementEvent.DeleteUser(key))
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsView() {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Notification Message") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StartColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = StartColor
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                Toast.makeText(context, "Notification Sent!", Toast.LENGTH_SHORT).show()
                message = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = StartColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send Notification")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Notification")
        }
    }
}
