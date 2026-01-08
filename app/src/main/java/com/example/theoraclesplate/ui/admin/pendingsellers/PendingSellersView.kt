package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun PendingSellersView(viewModel: PendingSellersViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(state.pendingSellers) { (key, user) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(user.email, color = Color.White.copy(alpha = 0.7f))
                    }
                    Button(
                        onClick = {
                            viewModel.onEvent(PendingSellersEvent.ApproveSeller(key))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StartColor)
                    ) {
                        Text("Approve")
                    }
                }
            }
        }
    }
}
