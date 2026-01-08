package com.example.theoraclesplate.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.User
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar


@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = remember { Firebase.auth }
    var isLoading by remember { mutableStateOf(false) }


    fun checkRoleAndNavigate(userId: String) {
        val database = Firebase.database.reference
        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                isLoading = false
                when (user.role) {
                    "seller" -> navController.navigate("seller_dashboard") { popUpTo("start") { inclusive = true } }
                    "admin" -> navController.navigate("admin_panel") { popUpTo("start") { inclusive = true } }
                    else -> navController.navigate("home") { popUpTo("start") { inclusive = true } }
                }
            } else {
                val firebaseUser = auth.currentUser
                val newUser = User(
                    name = firebaseUser?.displayName ?: name.ifEmpty { "User" },
                    email = firebaseUser?.email ?: email,
                    role = "buyer",
                    profileImage = firebaseUser?.photoUrl?.toString() ?: ""
                )
                
                database.child("users").child(userId).setValue(newUser)
                    .addOnSuccessListener {
                        isLoading = false
                        navController.navigate("home") { popUpTo("start") { inclusive = true } }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        if (e.message?.contains("Permission denied") == true) {
                             Toast.makeText(context, "Warning: Profile creation failed (Permission Denied).", Toast.LENGTH_LONG).show()
                             navController.navigate("home") { popUpTo("start") { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Failed to create user profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }.addOnFailureListener { e ->
            isLoading = false
            val errorMessage = e.message ?: "Unknown Error"
            if (errorMessage.contains("Permission denied")) {
                Toast.makeText(context, "DB Error: Check Firebase Rules.", Toast.LENGTH_LONG).show()
            } else if (errorMessage.contains("Client is offline")) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "DB Error: $errorMessage", Toast.LENGTH_LONG).show()
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
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                isLoading = true
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) checkRoleAndNavigate(userId)
                        } else {
                            isLoading = false
                            Toast.makeText(context, "Google Sign-In failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                isLoading = false
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)) // Dark background
    ) {
        
        AnimatedCircleBackground(modifier = Modifier.fillMaxSize())

        // --- FOREGROUND CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(90.dp)
            )

            Text(
                text = "Sign Up",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White, // Text color changed for dark bg
                modifier = Modifier.padding(vertical = 16.dp)
            )

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StartColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = StartColor
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser = auth.currentUser
                                    val userId = firebaseUser?.uid
                                    if (userId != null) {
                                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener {
                                             val user = User(name = name, email = email, role = "buyer")
                                             Firebase.database.reference.child("users").child(userId).setValue(user)
                                                 .addOnSuccessListener {
                                                     isLoading = false
                                                     navController.navigate("home") { popUpTo("start") { inclusive = true } }
                                                 }
                                                 .addOnFailureListener { e ->
                                                     isLoading = false
                                                     Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                                 }
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    val exception = task.exception
                                    val errorMessage = if (exception?.message?.contains("The email address is already in use") == true) {
                                        "Email already registered"
                                    } else {
                                        exception?.localizedMessage ?: "Registration failed"
                                    }
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Create Account", fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f))
                Text(" OR ", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    try {
                        val clientId = context.getString(R.string.default_web_client_id)
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(clientId).requestEmail().build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Config Error: Check google-services.json", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.9f), contentColor = Color.Black),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                shape = MaterialTheme.shapes.medium
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon_1),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign up with Google", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Login", color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}
