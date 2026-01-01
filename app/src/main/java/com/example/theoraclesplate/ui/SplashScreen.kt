package com.example.theoraclesplate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val auth = Firebase.auth
    val database = Firebase.database.reference

    LaunchedEffect(key1 = true) {
        delay(2000) // Short delay to show logo
        val currentUser = auth.currentUser
        
        if (currentUser != null) {
            // User is logged in, check role
            database.child("users").child(currentUser.uid).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)
                    val route = when (user?.role) {
                        "seller" -> "seller_dashboard"
                        "admin" -> "admin_panel"
                        "driver" -> "delivery_dashboard"
                        else -> "home"
                    }
                    navController.navigate(route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                .addOnFailureListener {
                    // Fallback to start if network fails
                    navController.navigate("start") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
        } else {
            // No user logged in
            navController.navigate("start") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Assuming logo exists
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}
