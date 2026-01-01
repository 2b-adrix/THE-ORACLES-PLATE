package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.OrderItem
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun CheckoutScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val database = Firebase.database
    val currentUser = auth.currentUser
    
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isPlacingOrder by remember { mutableStateOf(false) }
    
    // Payment State
    var paymentMethod by remember { mutableStateOf("COD") } // COD or CARD
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCVC by remember { mutableStateOf("") }
    
    val cartItems = remember { mutableStateListOf<CartItemData>() }
    var totalPrice by remember { mutableStateOf(0.0) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val cartRef = database.reference.child("users").child(currentUser.uid).child("cart")
            cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartItems.clear()
                    var total = 0.0
                    for (child in snapshot.children) {
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val priceStr = child.child("price").getValue(String::class.java) ?: "0"
                        val quantity = child.child("quantity").getValue(Int::class.java) ?: 1
                        val image = child.child("image").getValue(String::class.java) ?: ""
                        
                        val price = priceStr.replace("$", "").toDoubleOrNull() ?: 0.0
                        total += price * quantity
                        
                        cartItems.add(CartItemData(child.key ?: "", name, priceStr, image, quantity))
                    }
                    totalPrice = total
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
             IconButton(onClick = { navController.popBackStack() }) {
                 Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = StartColor)
             }
            Text(
                text = "Checkout",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = address, 
            onValueChange = { address = it },
            label = { Text("Street Address") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = city, 
            onValueChange = { city = it },
            label = { Text("City / Zip Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = phoneNumber, 
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            PaymentOption(
                title = "Cash on Delivery", 
                selected = paymentMethod == "COD",
                onClick = { paymentMethod = "COD" }
            )
            Spacer(modifier = Modifier.width(8.dp))
            PaymentOption(
                title = "Credit Card", 
                selected = paymentMethod == "CARD",
                onClick = { paymentMethod = "CARD" }
            )
        }
        
        if (paymentMethod == "CARD") {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16) cardNumber = it },
                label = { Text("Card Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = cardExpiry,
                    onValueChange = { if (it.length <= 5) cardExpiry = it },
                    label = { Text("MM/YY") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = cardCVC,
                    onValueChange = { if (it.length <= 3) cardCVC = it },
                    label = { Text("CVC") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        cartItems.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.quantity} x ${item.name}", color = Color.Gray)
                Text(item.price, fontWeight = FontWeight.SemiBold)
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("$${String.format("%.2f", totalPrice)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StartColor)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if (address.isNotEmpty() && city.isNotEmpty() && phoneNumber.isNotEmpty()) {
                    if (paymentMethod == "CARD" && (cardNumber.length < 16 || cardExpiry.isEmpty() || cardCVC.length < 3)) {
                        Toast.makeText(context, "Please enter valid card details", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    if (currentUser != null && cartItems.isNotEmpty()) {
                        isPlacingOrder = true
                        val historyRef = database.reference.child("users").child(currentUser.uid).child("order_history")
                        val cartRef = database.reference.child("users").child(currentUser.uid).child("cart")
                        val ordersRef = database.reference.child("orders")
                        
                        val timestamp = System.currentTimeMillis()
                        val fullAddress = "$address, $city\nPhone: $phoneNumber"
                        
                        val orderItems = cartItems.map { 
                            OrderItem(it.name, it.price, it.image, it.quantity) 
                        }
                        
                        val orderId = ordersRef.push().key ?: timestamp.toString()
                        val order = Order(
                            orderId = orderId,
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Unknown",
                            items = orderItems,
                            totalAmount = "$${String.format("%.2f", totalPrice)}",
                            address = fullAddress,
                            paymentMethod = paymentMethod,
                            status = "Pending",
                            timestamp = timestamp
                        )
                        
                        // Save to Global Orders
                        ordersRef.child(orderId).setValue(order)
                        
                        // Save to User History
                        historyRef.child(orderId).setValue(order)
                        
                        // Clear cart
                        cartRef.removeValue()
                            .addOnSuccessListener {
                                isPlacingOrder = false
                                Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                            .addOnFailureListener {
                                isPlacingOrder = false
                                Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(context, "Please fill in all delivery details", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isPlacingOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StartColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isPlacingOrder) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(if(paymentMethod == "COD") "Place Order" else "Pay & Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PaymentOption(title: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) StartColor else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (selected) StartColor.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) StartColor else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
