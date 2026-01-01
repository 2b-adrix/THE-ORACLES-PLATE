package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(navController: NavController) {
    val auth = Firebase.auth
    val database = Firebase.database
    val currentUser = auth.currentUser
    val context = LocalContext.current

    val historyItems = remember { mutableStateListOf<HistoryItem>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val historyRef = database.reference.child("users").child(currentUser.uid).child("order_history")
            historyRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    historyItems.clear()
                    for (child in snapshot.children) {
                        val orderId = child.key ?: ""
                        
                        val status = child.child("status").getValue(String::class.java) ?: "Completed"
                        val totalAmount = child.child("totalAmount").getValue(String::class.java) ?: ""
                        val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                        
                        // Construct a display name from items if possible
                        var displayName = "Order"
                        val itemsSnapshot = child.child("items")
                        if (itemsSnapshot.exists()) {
                            val firstItemName = itemsSnapshot.children.firstOrNull()?.child("name")?.getValue(String::class.java)
                            val count = itemsSnapshot.childrenCount
                            if (firstItemName != null) {
                                displayName = if (count > 1) "$firstItemName + ${count - 1} more" else firstItemName
                            }
                        } else {
                             // Fallback for old data structure
                             displayName = child.child("name").getValue(String::class.java) ?: "Food Item"
                        }
                        
                         val image = try {
                            if (itemsSnapshot.exists()) {
                                itemsSnapshot.children.firstOrNull()?.child("image")?.getValue(String::class.java) ?: ""
                            } else {
                                child.child("image").getValue(String::class.java) ?: ""
                            }
                        } catch (e: Exception) { "" }


                        val date = if (timestamp > 0) {
                            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
                        } else {
                            "Unknown Date"
                        }

                        historyItems.add(HistoryItem(orderId, displayName, totalAmount, date, image, status))
                    }
                    // Reverse to show newest first
                    historyItems.sortByDescending { 
                         try {
                            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).parse(it.date)?.time ?: 0L
                         } catch (e: Exception) { 0L }
                    }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                    // CHANGED: Show exact error message
                    Toast.makeText(context, "History Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
             IconButton(onClick = { navController.popBackStack() }) {
                 Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = StartColor)
             }
            Text(
                text = "Order History",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (isLoading) {
             Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else if (historyItems.isEmpty()) {
             Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No order history", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(historyItems) { item ->
                    HistoryItemRow(item, 
                        onReorder = {
                            navController.navigate("home")
                        },
                        onCancel = {
                            if (currentUser != null) {
                                database.reference.child("orders").child(item.orderId).removeValue()
                                database.reference.child("users").child(currentUser.uid).child("order_history").child(item.orderId).child("status").setValue("Cancelled")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItem, onReorder: () -> Unit, onCancel: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                 modifier = Modifier.size(60.dp)
            ) {
                AsyncImage(
                    model = if (item.image.isNotEmpty()) item.image else R.drawable.food1,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(item.price, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = StartColor)
                Text(item.date, fontSize = 12.sp, color = Color.Gray)
                Text("Status: ${item.status}", fontSize = 12.sp, fontWeight = FontWeight.Bold, 
                    color = if(item.status == "Pending") Color.Blue else if(item.status == "Cancelled") Color.Red else Color.Green)
            }

            if (item.status == "Pending") {
                 Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Cancel", fontSize = 12.sp, color = Color.White)
                }
            } else {
                 Button(
                    onClick = onReorder,
                    colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Reorder", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

data class HistoryItem(val orderId: String, val name: String, val price: String, val date: String, val image: String, val status: String)
