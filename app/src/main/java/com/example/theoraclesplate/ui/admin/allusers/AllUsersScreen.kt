package com.example.theoraclesplate.ui.admin.allusers

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
import com.example.theoraclesplate.ui.admin.pendingsellers.UserCard

@Composable
fun AllUsersScreen(viewModel: AllUsersViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            // Show a loading indicator
        } else if (state.error != null) {
            // Show an error message
        } else {
            LazyColumn {
                items(state.users) { user ->
                    UserCard(
                        user = user,
                        onApprove = { /* Not used here */ },
                        onDecline = { viewModel.onEvent(AllUsersEvent.DeleteUser(user.uid)) }
                    )
                }
            }
        }
    }
}
