package com.example.theoraclesplate.ui.admin.pendingsellers.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.theoraclesplate.model.User

@Composable
fun UserCard(user: User, actions: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${user.name}")
            Text(text = "Email: ${user.email}")
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                actions()
            }
        }
    }
}
