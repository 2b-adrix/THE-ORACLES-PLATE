package com.example.theoraclesplate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun StartScreen(navController: NavController) {

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
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.height(150.dp)
            )

            Text(
                text = "The Oracle's Plate",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
             Text(
                text = "Discover the best food in town",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(text = "Get Started", fontSize = 18.sp, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
