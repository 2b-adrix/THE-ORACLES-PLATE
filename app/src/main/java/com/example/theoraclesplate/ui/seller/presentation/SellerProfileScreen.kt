package com.example.theoraclesplate.ui.seller.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SellerProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    SellerProfileScreenContent(
        userName = user?.displayName ?: "Unknown",
        userEmail = user?.email ?: "Unknown",
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreenContent(
    userName: String,
    userEmail: String,
    onBack: () -> Unit
) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("My Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Name: $userName", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Email: $userEmail", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SellerProfileScreenPreview() {
    THEORACLESPLATETheme {
        SellerProfileScreenContent(
            userName = "Gordon Ramsay",
            userEmail = "gordon@kitchen.com",
            onBack = {}
        )
    }
}
