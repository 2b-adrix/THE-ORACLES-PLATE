package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.model.User
import com.example.theoraclesplate.ui.admin.pendingsellers.PendingSellersEvent
import com.example.theoraclesplate.ui.admin.pendingsellers.PendingSellersViewModel

@Composable
fun PendingSellersScreen(
    viewModel: PendingSellersViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pending Sellers", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.pendingSellers) { user ->
                    PendingSellerItem(user = user, onApprove = { viewModel.onEvent(PendingSellersEvent.ApproveSeller(user.uid)) }, onDecline = { viewModel.onEvent(PendingSellersEvent.DeclineSeller(user.uid)) })
                }
            }
        }
    }
}

@Composable
private fun PendingSellerItem(user: User, onApprove: () -> Unit, onDecline: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
            }
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
}