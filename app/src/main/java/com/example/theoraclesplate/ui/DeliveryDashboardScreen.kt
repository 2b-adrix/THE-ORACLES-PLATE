package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
    val database = Firebase.database
    val orders = remember { mutableStateListOf<Order>() }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    
    // Filter for orders that are relevant to delivery
    val relevantOrders = orders.filter { 
        it.status == "Ready" || it.status == "Out for Delivery" 
    }.sortedByDescending { it.timestamp }

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
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                // CHANGED: Show detailed error message
                Toast.makeText(context, "Delivery Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Delivery Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Logout",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    Firebase.auth.signOut()
                    navController.navigate("login") {
                        popUpTo("delivery_dashboard") { inclusive = true }
                    }
                }
            )
        }
        
        Text("Available for Pickup & Delivery", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else if (relevantOrders.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No active deliveries", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(relevantOrders) { order ->
                    DeliveryOrderItem(order)
                }
            }
        }
    }
}

@Composable
fun DeliveryOrderItem(order: Order) {
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
                Text(order.status, fontWeight = FontWeight.Bold, color = if (order.status == "Ready") StartColor else Color(0xFF4CAF50))
            }
            Text("Customer: ${order.userName}", fontSize = 14.sp, color = Color.Gray)
            
            // Show Address
            Text(
                text = "Address: ${if (order.address.isNotEmpty()) order.address else "Pickup at Counter"}", 
                fontSize = 14.sp, 
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            
            // Show Payment Info
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Text("Payment: ", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = order.paymentMethod, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold,
                    color = if (order.paymentMethod == "COD") Color.Red else Color.Blue
                )
                Text(" (${order.totalAmount})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Action Button
            Button(
                onClick = {
                    if (order.status == "Ready") {
                        database.reference.child("orders").child(order.orderId).child("status").setValue("Out for Delivery")
                        // Also update user history
                         database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).child("status").setValue("Out for Delivery")
                    } else if (order.status == "Out for Delivery") {
                        database.reference.child("orders").child(order.orderId).child("status").setValue("Completed")
                        database.reference.child("users").child(order.userId).child("order_history").child(order.orderId).child("status").setValue("Completed")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (order.status == "Ready") StartColor else Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = if (order.status == "Ready") "Pick Up Order" else "Mark as Delivered",
                    color = Color.White
                )
            }
        }
    }
}
