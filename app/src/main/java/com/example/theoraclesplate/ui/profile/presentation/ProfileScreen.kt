package com.example.theoraclesplate.ui.profile.presentation

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppCard
import com.example.theoraclesplate.ui.components.CardStyle
import com.example.theoraclesplate.ui.profile.viewmodel.ProfileEvent
import com.example.theoraclesplate.ui.profile.viewmodel.ProfileState
import com.example.theoraclesplate.ui.profile.viewmodel.ProfileViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    rootNavController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ProfileViewModel.UiEvent.Logout -> {
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    rootNavController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        }
    }

    ProfileScreenContent(
        state = state,
        onLogoutClick = { viewModel.onEvent(ProfileEvent.Logout) },
        onEditProfileClick = { rootNavController.navigate("edit_profile") },
        onHistoryClick = { rootNavController.navigate("history") },
        onPaymentClick = { Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show() },
        onSupportClick = { Toast.makeText(context, "support@oracleplate.com", Toast.LENGTH_LONG).show() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(500))
    }

    PremiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .alpha(alpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    AsyncImage(
                        model = state.user?.photoUrl ?: R.drawable.logo,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = state.user?.displayName ?: "User Name",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = state.user?.email ?: "user@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(40.dp))

                ProfileMenuItem(Icons.Default.Edit, "Edit Profile", onClick = onEditProfileClick)
                ProfileMenuItem(Icons.Default.Payment, "Payment Methods", onClick = onPaymentClick)
                ProfileMenuItem(Icons.Default.History, "Order History", onClick = onHistoryClick)
                ProfileMenuItem(Icons.Default.SupportAgent, "Contact Support", onClick = onSupportClick)
                
                Spacer(modifier = Modifier.weight(1f))
                
                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp, 
                    text = "Log Out", 
                    onClick = onLogoutClick, 
                    isDestructive = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit, isDestructive: Boolean = false) {
    AppCard(
        style = CardStyle.Glass,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (isDestructive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text, 
                style = MaterialTheme.typography.bodyLarge, 
                fontWeight = FontWeight.SemiBold, 
                color = if (isDestructive) MaterialTheme.colorScheme.primary else Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight, 
                contentDescription = null, 
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    THEORACLESPLATETheme {
        ProfileScreenContent(
            state = ProfileState(),
            onLogoutClick = {},
            onEditProfileClick = {},
            onHistoryClick = {},
            onPaymentClick = {},
            onSupportClick = {}
        )
    }
}
