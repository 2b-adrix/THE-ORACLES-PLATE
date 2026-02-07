package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(rootNavController: NavController) {
    val auth = Firebase.auth
    val user = auth.currentUser
    val context = LocalContext.current

    val profileImageUrl = user?.photoUrl
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        delay(200)
        alpha.animateTo(1f, animationSpec = tween(800))
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
        containerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Transparent)
                .padding(16.dp)
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = profileImageUrl ?: R.drawable.logo,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.displayName ?: "User Name",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user?.email ?: "user@example.com",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileMenuItem(Icons.Default.Edit, "Edit Profile", onClick = { rootNavController.navigate("edit_profile") })
            ProfileMenuItem(Icons.Default.Payment, "Payment Methods", onClick = { Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show() })
            ProfileMenuItem(Icons.Default.History, "Order History", onClick = { rootNavController.navigate("history") })
            ProfileMenuItem(Icons.Default.SupportAgent, "Contact Support", onClick = {
                Toast.makeText(context, "Email support@oracleplate.com", Toast.LENGTH_LONG).show()
            })
            ProfileMenuItem(Icons.Default.ExitToApp, "Log Out", onClick = {
                auth.signOut()

                try {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInClient.signOut()
                } catch (e: Exception) {
                    // Ignore Google sign out errors
                }

                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                rootNavController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }, isDestructive = true)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit, isDestructive: Boolean = false) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive) StartColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isDestructive) StartColor else Color.White)
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = if (isDestructive) StartColor else Color.White)
        }
    }
}
