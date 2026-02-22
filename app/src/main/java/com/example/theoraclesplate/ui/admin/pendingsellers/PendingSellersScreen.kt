package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PendingSellersScreen(
    viewModel: PendingSellersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pending Sellers", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.pendingSellers) { user ->
                    PendingSellerItem(user.name, onApprove = { viewModel.onEvent(PendingSellersEvent.ApproveSeller(user.uid)) }, onDecline = { viewModel.onEvent(PendingSellersEvent.DeclineSeller(user.uid)) })
                }
            }
        }
    }
}

@Composable
private fun PendingSellerItem(name: String, onApprove: () -> Unit, onDecline: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name)
        Row {
            Button(onClick = onApprove) {
                Text("Approve")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDecline) {
                Text("Decline")
            }
        }
    }
}
