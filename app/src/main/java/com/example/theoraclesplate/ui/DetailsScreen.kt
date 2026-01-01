package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun DetailsScreen(navController: NavController, foodName: String?, foodPrice: String?, foodImage: String?) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val database = Firebase.database
    
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
             Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = StartColor)) {
                 Text("< Back", fontSize = 18.sp)
             }
             
             Text(
                text = foodName ?: "Food Name",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(250.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                AsyncImage(
                    model = foodImage ?: "https://picsum.photos/200/300", // Fallback image
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Text(
                text = "Price: $foodPrice",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = StartColor,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            Text(
                text = "Description",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    val user = auth.currentUser
                    if (user != null) {
                        val cartItem = hashMapOf(
                            "name" to foodName,
                            "price" to foodPrice,
                            "quantity" to 1,
                            "image" to (foodImage ?: "")
                        )
                        database.reference.child("users").child(user.uid).child("cart").push().setValue(cartItem)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add To Cart", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
