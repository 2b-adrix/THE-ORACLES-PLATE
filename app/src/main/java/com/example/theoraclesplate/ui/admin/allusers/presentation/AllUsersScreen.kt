package com.example.theoraclesplate.ui.admin.allusers.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.model.User
import com.example.theoraclesplate.ui.admin.allusers.viewmodel.AllUsersEvent
import com.example.theoraclesplate.ui.admin.allusers.viewmodel.AllUsersState
import com.example.theoraclesplate.ui.admin.allusers.viewmodel.AllUsersViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun AllUsersScreen(viewModel: AllUsersViewModel = hiltViewModel()) {
    val state by viewModel.state

    AllUsersScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersScreenContent(
    state: AllUsersState,
    onEvent: (AllUsersEvent) -> Unit
) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("All Users", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error, color = Color.Red)
                    }
                } else if (state.users.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No users found.", color = Color.White)
                    }
                } else {
                    LazyColumn {
                        items(state.users) { user ->
                            UserItem(
                                user = user,
                                onDelete = { onEvent(AllUsersEvent.DeleteUser(user.uid)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name.ifEmpty { "Unknown" }, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = user.email, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                Text(text = "Role: ${user.role.replaceFirstChar { it.uppercase() }}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red.copy(alpha = 0.8f))
            }
        }
    }
}

@Preview
@Composable
fun AllUsersScreenPreview() {
    THEORACLESPLATETheme {
        AllUsersScreenContent(
            state = AllUsersState(
                users = listOf(
                    User(uid = "1", name = "Admin User", email = "admin@test.com", role = "admin"),
                    User(uid = "2", name = "John Seller", email = "john@test.com", role = "seller")
                ),
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
