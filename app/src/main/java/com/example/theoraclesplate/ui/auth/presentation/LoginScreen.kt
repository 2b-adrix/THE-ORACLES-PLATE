package com.example.theoraclesplate.ui.auth.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.auth.viewmodel.LoginEvent
import com.example.theoraclesplate.ui.auth.viewmodel.LoginState
import com.example.theoraclesplate.ui.auth.viewmodel.LoginViewModel
import com.example.theoraclesplate.ui.components.AppButton
import com.example.theoraclesplate.ui.components.AppCard
import com.example.theoraclesplate.ui.components.AppTextField
import com.example.theoraclesplate.ui.components.ButtonStyle
import com.example.theoraclesplate.ui.components.CardStyle
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.UiEvent.LoginSuccess -> {
                    val destination = when (event.role) {
                        "admin" -> "admin_dashboard"
                        "seller" -> "seller_dashboard"
                        "delivery" -> "delivery_dashboard"
                        else -> "home"
                    }
                    navController.navigate(destination) {
                        popUpTo("start") { inclusive = true }
                    }
                }
                is LoginViewModel.UiEvent.SignupSuccess -> {
                    Toast.makeText(context, "Signup successful!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let {
                    viewModel.onEvent(LoginEvent.LoginWithGoogle(it))
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onSignInWithGoogle = {
            try {
                val clientId = context.getString(R.string.default_web_client_id)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(clientId)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Config Error: Check google-services.json", Toast.LENGTH_LONG).show()
            }
        },
        onNavigateToSignup = { navController.navigate("signup") }
    )
}

@Composable
fun LoginScreenContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    PremiumBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Login to continue your journey",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            AppCard(
                style = CardStyle.Glass,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppTextField(
                    value = state.email,
                    onValueChange = { onEvent(LoginEvent.EnteredEmail(it)) },
                    label = "Email Address",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = state.password,
                    onValueChange = { onEvent(LoginEvent.EnteredPassword(it)) },
                    label = "Password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End).clickable { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(
                    text = "Login",
                    onClick = { onEvent(LoginEvent.Login) },
                    isLoading = state.isLoading,
                    style = ButtonStyle.Gradient,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
                Text(" OR ", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppButton(
                text = "Sign in with Google",
                onClick = onSignInWithGoogle,
                style = ButtonStyle.Outlined,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon_1),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onNavigateToSignup) {
                Text(
                    text = "Don\'t have an account? Sign Up",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    THEORACLESPLATETheme {
        LoginScreenContent(
            state = LoginState(),
            onEvent = {},
            onSignInWithGoogle = {},
            onNavigateToSignup = {}
        )
    }
}
