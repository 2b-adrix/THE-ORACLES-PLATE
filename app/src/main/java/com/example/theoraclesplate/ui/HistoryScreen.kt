package com.example.theoraclesplate.ui

import androidx.compose.foundation.Image
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
    
    val historyItems = remember { mutableStateListOf<HistoryItem>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val historyRef = database.reference.child("users").child(currentUser.uid).child("order_history")
            historyRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    historyItems.clear()
                    for (child in snapshot.children) {
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(String::class.java) ?: ""
                        val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                        
                        val date = if (timestamp > 0) {
                            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
                        } else {
                            "Unknown Date"
                        }
                        
                        historyItems.add(HistoryItem(name, price, date, R.drawable.food1))
                    }
                    // Reverse to show newest first
                    historyItems.reverse()
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
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
                    HistoryItemRow(item)
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItem) {
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
                Image(
                    painter = painterResource(id = item.image),
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
            }
            
            Button(
                onClick = { /* Reorder logic */ },
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

data class HistoryItem(val name: String, val price: String, val date: String, val image: Int)
