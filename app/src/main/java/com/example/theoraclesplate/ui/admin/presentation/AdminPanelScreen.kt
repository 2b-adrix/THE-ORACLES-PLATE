package com.example.theoraclesplate.ui.admin.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.model.User
import com.example.theoraclesplate.ui.admin.viewmodel.AdminPanelEvent
import com.example.theoraclesplate.ui.admin.viewmodel.AdminPanelState
import com.example.theoraclesplate.ui.admin.viewmodel.AdminPanelViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AdminPanelScreen(
    navController: NavController,
    viewModel: AdminPanelViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AdminPanelViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AdminPanelScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onLogout = {
            auth.signOut()
            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()
            } catch (e: Exception) {
                // Ignore
            }
            navController.navigate("login") {
                popUpTo("admin_panel") { inclusive = true }
            }
        }
    )
}

@Composable
fun AdminPanelScreenContent(
    state: AdminPanelState,
    onEvent: (AdminPanelEvent) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)) // Themed for dark mode
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Admin Panel", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Logout",
                color = StartColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLogout() }
            )
        }
        
        Text("Manage User Roles", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(vertical = 8.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else {
            LazyColumn {
                items(state.users) { user ->
                    UserRoleItem(user) { newRole ->
                        onEvent(AdminPanelEvent.ChangeRole(user.uid, newRole))
                    }
                }
            }
        }
    }
}

@Composable
fun UserRoleItem(user: User, onChangeRole: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("buyer", "seller", "admin", "driver")
    
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)) // Glassmorphism
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name.ifEmpty { "Unknown" }, fontWeight = FontWeight.Bold, color = Color.White)
                Text(user.email, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                Text("Current Role: ${user.role}", fontSize = 12.sp, color = StartColor)
            }
            
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Change Role", color = Color.White, fontSize = 12.sp)
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF2C2C4E))
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.replaceFirstChar { it.uppercase() }, color = Color.White) },
                            onClick = {
                                onChangeRole(role)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AdminPanelScreenPreview() {
    THEORACLESPLATETheme {
        AdminPanelScreenContent(
            state = AdminPanelState(
                users = listOf(
                    User(uid = "1", name = "John Doe", email = "john@example.com", role = "buyer"),
                    User(uid = "2", name = "Jane Smith", email = "jane@example.com", role = "seller")
                ),
                isLoading = false
            ),
            onEvent = {},
            onLogout = {}
        )
    }
}
