package com.example.theoraclesplate.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor
import kotlinx.coroutines.delay

@Composable
fun StartScreen(navController: NavController) {

    val alpha1 = remember { Animatable(0f) }
    val alpha2 = remember { Animatable(0f) }
    val alpha3 = remember { Animatable(0f) }
    val alpha4 = remember { Animatable(0f) }
    val alpha5 = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        delay(200)
        alpha1.animateTo(1f, animationSpec = tween(800))
        delay(200)
        alpha2.animateTo(1f, animationSpec = tween(800))
        delay(200)
        alpha3.animateTo(1f, animationSpec = tween(800))
        delay(200)
        alpha4.animateTo(1f, animationSpec = tween(800))
        delay(200)
        alpha5.animateTo(1f, animationSpec = tween(800))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        AnimatedCircleBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(150.dp)
                    .alpha(alpha1.value)
            )

            Text(
                text = "The Oracle\'s Plate",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.alpha(alpha1.value)
            )

            Text(
                text = "Discover the best food in town",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(alpha2.value)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(alpha3.value),
                colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(text = "Continue as a Buyer", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("seller_login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(alpha4.value),
                border = BorderStroke(1.dp, StartColor),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Seller Login/Signup", fontSize = 18.sp, color = StartColor)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("delivery_login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(alpha5.value),
                border = BorderStroke(1.dp, StartColor),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Delivery Login", fontSize = 18.sp, color = StartColor)
            }

            Spacer(modifier = Modifier.weight(0.5f))

            TextButton(onClick = { navController.navigate("admin_login") }) {
                Text(text = "Admin Login", color = Color.White.copy(alpha = 0.7f))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
