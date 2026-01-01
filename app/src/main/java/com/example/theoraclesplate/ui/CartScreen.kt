package com.example.theoraclesplate.ui

import android.widget.Toast
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
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
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
fun CartScreen(rootNavController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val database = Firebase.database
    val currentUser = auth.currentUser
    
    val cartItems = remember { mutableStateListOf<CartItemData>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val cartRef = database.reference.child("users").child(currentUser.uid).child("cart")
            cartRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartItems.clear()
                    for (child in snapshot.children) {
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(String::class.java) ?: ""
                        val quantity = child.child("quantity").getValue(Int::class.java) ?: 1
                        val image = child.child("image").getValue(String::class.java) ?: ""

                        cartItems.add(CartItemData(child.key ?: "", name, price, image, quantity))
                    }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load cart: ${error.message}", Toast.LENGTH_SHORT).show()
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
    ) {
        Text(
            text = "Your Cart",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else if (cartItems.isEmpty()) {
             Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(item, 
                        onIncrease = { 
                            if (currentUser != null) {
                                database.reference.child("users").child(currentUser.uid).child("cart")
                                    .child(item.id).child("quantity").setValue(item.quantity + 1)
                            }
                        },
                        onDecrease = { 
                            if (item.quantity > 1 && currentUser != null) {
                                database.reference.child("users").child(currentUser.uid).child("cart")
                                    .child(item.id).child("quantity").setValue(item.quantity - 1)
                            }
                        },
                        onDelete = { 
                             if (currentUser != null) {
                                database.reference.child("users").child(currentUser.uid).child("cart")
                                    .child(item.id).removeValue()
                            }
                        }
                    )
                }
            }
            
            Button(
                onClick = { 
                    if (currentUser != null && cartItems.isNotEmpty()) {
                        rootNavController.navigate("checkout_screen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Proceed to Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemData, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit) {
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
                 modifier = Modifier.size(80.dp)
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
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color=Color.Black)
                Text(item.price, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color=Color.Black)
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp).background(StartColor, RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(painterResource(R.drawable.minus), null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(painterResource(R.drawable.plus), null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(painterResource(R.drawable.trash), null, tint = Color.Gray)
            }
        }
    }
}

data class CartItemData(val id: String, val name: String, val price: String, val image: String, val quantity: Int)
