package com.example.theoraclesplate.ui.admin.allusers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AllUsersView(viewModel: AllUsersViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(state.users) { (key, user) ->
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
                        Text("Role: ${user.role}", color = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = { 
                        viewModel.onEvent(AllUsersEvent.DeleteUser(key))
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
                    }
                }
            }
        }
    }
}
