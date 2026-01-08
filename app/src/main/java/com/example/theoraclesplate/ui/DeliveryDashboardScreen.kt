package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun DeliveryDashboardScreen(navController: NavController) {
    val auth = Firebase.auth
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ready for Pickup", "Out for Delivery", "Delivered")

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
            Text("Delivery Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Logout",
                color = StartColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    auth.signOut()
                    navController.navigate("start") {
                        popUpTo("delivery_dashboard") { inclusive = true }
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

        when (selectedTab) {
            0 -> ReadyForPickupView()
            1 -> OutForDeliveryView()
            2 -> DeliveredView()
        }
    }
}

@Composable
fun ReadyForPickupView() {
    val database = Firebase.database
    val orders = remember { mutableStateListOf<Pair<String, Order>>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val ordersRef = database.reference.child("orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    if (order != null && order.status == "Ready") {
                        orders.add(Pair(child.key!!, order))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching orders: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(orders) { (key, order) ->
            DeliveryOrderCard(order = order, onUpdate = {
                database.reference.child("orders").child(key).child("status").setValue("Out for Delivery")
                database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).child("status").setValue("Out for Delivery")
            })
        }
    }
}

@Composable
fun OutForDeliveryView() {
    val database = Firebase.database
    val orders = remember { mutableStateListOf<Pair<String, Order>>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val ordersRef = database.reference.child("orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    if (order != null && order.status == "Out for Delivery") {
                        orders.add(Pair(child.key!!, order))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching orders: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(orders) { (key, order) ->
            DeliveryOrderCard(order = order, onUpdate = {
                database.reference.child("orders").child(key).child("status").setValue("Delivered")
                database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).child("status").setValue("Delivered")
            })
        }
    }
}

@Composable
fun DeliveredView() {
    val database = Firebase.database
    val orders = remember { mutableStateListOf<Order>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val ordersRef = database.reference.child("orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    if (order != null && order.status == "Delivered") {
                        orders.add(order)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching orders: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(orders) { order ->
            DeliveryOrderCard(order = order, onUpdate = null)
        }
    }
}

@Composable
fun DeliveryOrderCard(order: Order, onUpdate: (() -> Unit)?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("User: ${order.userName}", color = Color.White.copy(alpha = 0.7f))
            Text("Address: ${order.address}", color = Color.White.copy(alpha = 0.7f))
            Text("Total: ${order.totalAmount}", color = Color.White.copy(alpha = 0.7f))
            Text("Status: ${order.status}", color = when (order.status) {
                "Ready" -> Color.Yellow
                "Out for Delivery" -> Color.Cyan
                "Delivered" -> Color.Green
                else -> Color.White
            })

            onUpdate?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = it,
                        colors = ButtonDefaults.buttonColors(containerColor = if (order.status == "Ready") StartColor else Color(0xFF00C851))
                    ) {
                        Text(if (order.status == "Ready") "Pick Up Order" else "Mark as Delivered")
                    }
                }
            }
        }
    }
}
