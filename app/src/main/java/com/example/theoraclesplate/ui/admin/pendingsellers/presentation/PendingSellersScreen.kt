package com.example.theoraclesplate.ui.admin.pendingsellers.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.theoraclesplate.ui.admin.pendingsellers.viewmodel.PendingSellersEvent
import com.example.theoraclesplate.ui.admin.pendingsellers.viewmodel.PendingSellersState
import com.example.theoraclesplate.ui.admin.pendingsellers.viewmodel.PendingSellersViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun PendingSellersScreen(
    viewModel: PendingSellersViewModel = hiltViewModel()
) {
    val state by viewModel.state

    PendingSellersScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingSellersScreenContent(
    state: PendingSellersState,
    onEvent: (PendingSellersEvent) -> Unit
) {
    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Pending Sellers", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.pendingSellers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No pending sellers.", color = Color.White)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.pendingSellers) { user ->
                            PendingSellerItem(
                                user = user,
                                onApprove = { onEvent(PendingSellersEvent.ApproveSeller(user.uid)) },
                                onDecline = { onEvent(PendingSellersEvent.DeclineSeller(user.uid)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingSellerItem(user: User, onApprove: () -> Unit, onDecline: () -> Unit) {
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
            }
            Row {
                IconButton(onClick = onApprove) {
                    Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color.Green.copy(alpha = 0.8f))
                }
                IconButton(onClick = onDecline) {
                    Icon(Icons.Default.Close, contentDescription = "Decline", tint = Color.Red.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Preview
@Composable
fun PendingSellersScreenPreview() {
    THEORACLESPLATETheme {
        PendingSellersScreenContent(
            state = PendingSellersState(
                pendingSellers = listOf(
                    User(uid = "1", name = "Gordon Ramsay", email = "gordon@kitchen.com"),
                    User(uid = "2", name = "Jamie Oliver", email = "jamie@food.com")
                ),
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
