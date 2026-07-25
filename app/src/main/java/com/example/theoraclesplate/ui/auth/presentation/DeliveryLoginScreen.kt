    package com.example.theoraclesplate.ui.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.ui.common.AnimatedCircleBackground
import com.example.theoraclesplate.ui.auth.viewmodel.DeliveryAuthEvent
import com.example.theoraclesplate.ui.auth.viewmodel.DeliveryAuthState
import com.example.theoraclesplate.ui.auth.viewmodel.DeliveryAuthViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryLoginScreen(navController: NavController, viewModel: DeliveryAuthViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DeliveryAuthViewModel.UiEvent.AuthSuccess -> {
                    navController.navigate("delivery_dashboard") {
                        popUpTo("start") { inclusive = true }
                    }
                }

                is DeliveryAuthViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is DeliveryAuthViewModel.UiEvent.LogoutSuccess -> {
                    navController.navigate("start") {
                        popUpTo("start") { inclusive = true }
                    }
                }
            }
        }
    }

    DeliveryLoginScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToSignup = { navController.navigate("delivery_signup") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryLoginScreenContent(
    state: DeliveryAuthState,
    onEvent: (DeliveryAuthEvent) -> Unit,
    onNavigateToSignup: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Delivery Login") },
                actions = {
                    IconButton(onClick = { onEvent(DeliveryAuthEvent.Logout) }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedCircleBackground(modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onEvent(DeliveryAuthEvent.EnteredEmail(it)) },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onEvent(DeliveryAuthEvent.EnteredPassword(it)) },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(image, "toggle password visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onEvent(DeliveryAuthEvent.Login) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Login")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToSignup) {
                    Text(text = "Don't have an account? Sign up")
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeliveryLoginScreenPreview() {
    THEORACLESPLATETheme {
        DeliveryLoginScreenContent(
            state = DeliveryAuthState(),
            onEvent = {},
            onNavigateToSignup = {}
        )
    }
}
