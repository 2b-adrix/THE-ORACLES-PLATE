package com.example.theoraclesplate.ui.auth.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.onEvent(LoginEvent.LoginWithGoogle(idToken))
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.UiEvent.SignupSuccess -> {
                    navController.navigate("login_screen") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
                is LoginViewModel.UiEvent.LoginSuccess -> {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            }
        }
    }

    SignUpScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onSignInWithGoogle = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        },
        onNavigateToLogin = { navController.navigate("login_screen") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isPasswordValid = state.password.length >= 6

    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    val roles = listOf("buyer", "seller", "delivery")

    PremiumBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
             Text(
                text = "Begin your culinary adventure",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            AppCard(
                style = CardStyle.Glass,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppTextField(
                    value = state.name,
                    onValueChange = { onEvent(LoginEvent.EnteredName(it)) },
                    label = "Full Name",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    isError = state.password.isNotEmpty() && !isPasswordValid,
                    errorMessage = if (state.password.isNotEmpty() && !isPasswordValid) "At least 6 characters" else null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(image, null, tint = Color.White)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = isRoleDropdownExpanded,
                    onExpandedChange = { isRoleDropdownExpanded = !isRoleDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = state.role.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        label = { Text("I want to be a...") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isRoleDropdownExpanded,
                        onDismissRequest = { isRoleDropdownExpanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    onEvent(LoginEvent.EnteredRole(role))
                                    isRoleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(
                    text = "Sign Up",
                    onClick = { onEvent(LoginEvent.Signup) },
                    enabled = isPasswordValid,
                    isLoading = state.isLoading,
                    style = ButtonStyle.Gradient,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AppButton(
                text = "Sign up with Google",
                onClick = onSignInWithGoogle,
                style = ButtonStyle.Outlined,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                icon = {
                     Image(
                        painter = painterResource(id = R.drawable.google_icon_1),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Already have an account? Login",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    THEORACLESPLATETheme {
        SignUpScreenContent(
            state = LoginState(),
            onEvent = {},
            onSignInWithGoogle = {},
            onNavigateToLogin = {}
        )
    }
}
