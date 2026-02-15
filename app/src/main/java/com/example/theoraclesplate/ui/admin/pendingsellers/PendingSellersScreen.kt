package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PendingSellersScreen(viewModel: PendingSellersViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            // Show a loading indicator
        } else if (state.error != null) {
            // Show an error message
        } else {
            LazyColumn {
                items(state.pendingSellers) { user ->
                    UserCard(
                        user = user, 
                        onApprove = { viewModel.onEvent(PendingSellersEvent.ApproveSeller(user.uid)) }, 
                        onDecline = { viewModel.onEvent(PendingSellersEvent.DeclineSeller(user.uid)) }
                    )
                }
            }
        }
    }
}
