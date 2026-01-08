package com.example.theoraclesplate.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun EditProfileScreen(navController: NavController) {
    val auth = Firebase.auth
    val user = auth.currentUser
    val context = LocalContext.current

    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Init Cloudinary if not already initialized
    LaunchedEffect(Unit) {
        try {
            MediaManager.get()
        } catch (e: Exception) {
            try {
                // Initialize with your cloud name and unsigned preset
                val config = HashMap<String, String>()
                config["cloud_name"] = "dhlw6320" 
                config["secure"] = "true"
                MediaManager.init(context, config)
            } catch (e: Exception) {
                // Already initialized or config error
            }
        }
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)) // Themed for dark mode
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
             IconButton(onClick = { navController.popBackStack() }) {
                 Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = StartColor)
             }
            Text(
                text = "Edit Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White, // Themed for dark mode
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.clickable { 
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        ) {
            AsyncImage(
                model = selectedImageUri ?: user?.photoUrl ?: R.drawable.profile,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            
            // Camera Icon overlay
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .background(StartColor, CircleShape)
                    
            ) {
                Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
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
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColors
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    isLoading = true
                    
                    if (selectedImageUri != null) {
                        // Upload Image to Cloudinary first
                        MediaManager.get().upload(selectedImageUri)
                            .unsigned("unsigned_preset") // You need to set this up in Cloudinary Dashboard
                            .callback(object : UploadCallback {
                                override fun onStart(requestId: String) {}
                                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                                    val secureUrl = resultData["secure_url"] as? String
                                    
                                    if (secureUrl != null) {
                                        updateUserProfile(user, name, Uri.parse(secureUrl)) { success ->
                                            isLoading = false
                                            if (success) {
                                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
                                            } else {
                                                Toast.makeText(context, "Failed to update profile info.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, "Image upload failed: No URL returned.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onError(requestId: String, error: ErrorInfo) {
                                    isLoading = false
                                    Toast.makeText(context, "Image upload error: ${error.description}. Check Cloudinary Settings.", Toast.LENGTH_LONG).show()
                                }
                                override fun onReschedule(requestId: String, error: ErrorInfo) {}
                            })
                            .dispatch()
                    } else {
                        // Just update name
                        updateUserProfile(user, name, user?.photoUrl) { success ->
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StartColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Save Changes", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

fun updateUserProfile(user: com.google.firebase.auth.FirebaseUser?, name: String, photoUri: Uri?, onComplete: (Boolean) -> Unit) {
    val profileUpdates = UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .setPhotoUri(photoUri)
        .build()

    user?.updateProfile(profileUpdates)
        ?.addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
}
