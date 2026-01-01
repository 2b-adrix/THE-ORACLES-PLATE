package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.google.firebase.ktx.Firebase

@Composable
fun ProfileScreen(rootNavController: NavController) {
    val auth = Firebase.auth
    val user = auth.currentUser
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp).align(Alignment.Start)
        )
        
        Card(
            shape = CircleShape,
            modifier = Modifier.size(120.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = user?.displayName ?: "User Name",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = user?.email ?: "user@example.com",
            fontSize = 16.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ProfileMenuItem("Edit Profile", onClick = { rootNavController.navigate("edit_profile") })
        ProfileMenuItem("Payment Methods", onClick = { Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show() })
        ProfileMenuItem("Order History", onClick = { rootNavController.navigate("history") })
        
        // --- Add Support / Contact Us ---
        ProfileMenuItem("Contact Support", onClick = { 
            Toast.makeText(context, "Email support@oracleplate.com", Toast.LENGTH_LONG).show() 
        })
        
        ProfileMenuItem("Log Out", onClick = {
            auth.signOut()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            rootNavController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }, isDestructive = true)
    }
}

@Composable
fun ProfileMenuItem(text: String, onClick: () -> Unit, isDestructive: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDestructive) Color.Red.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.1f),
            contentColor = if (isDestructive) Color.Red else Color.Black
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
