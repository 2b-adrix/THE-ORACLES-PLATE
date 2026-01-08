package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.model.User
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun AdminPanelScreen(navController: NavController) {
    val database = Firebase.database
    val users = remember { mutableStateListOf<Pair<String, User>>() }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val auth = Firebase.auth

    LaunchedEffect(Unit) {
        val usersRef = database.reference.child("users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        users.add(Pair(child.key ?: "", user))
                    }
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Admin Panel", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Logout",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    auth.signOut()
                    try {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInClient.signOut()
                    } catch (e: Exception) {
                        // Ignore
                    }
                    navController.navigate("login") {
                        popUpTo("admin_panel") { inclusive = true }
                    }
                }
            )
        }
        
        Text("Manage User Roles", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else {
            LazyColumn {
                items(users) { (userId, user) ->
                    UserRoleItem(userId, user)
                }
            }
        }
    }
}
@Composable
fun UserRoleItem(userId: String, user: User) {
    val database = Firebase.database
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("buyer", "seller", "admin", "driver")
    
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name.ifEmpty { "Unknown" }, fontWeight = FontWeight.Bold)
                Text(user.email, fontSize = 12.sp, color = Color.Gray)
                Text("Current Role: ${user.role}", fontSize = 12.sp, color = StartColor)
            }
            
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Change Role", color = Color.Black, fontSize = 12.sp)
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.capitalize()) },
                            onClick = {
                                database.reference.child("users").child(userId).child("role").setValue(role)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
