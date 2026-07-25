package com.example.theoraclesplate.ui.profile.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppButton
import com.example.theoraclesplate.ui.profile.viewmodel.EditProfileViewModel
import com.example.theoraclesplate.ui.profile.viewmodel.UpdateState
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val updateState by viewModel.updateState.collectAsState()

    LaunchedEffect(updateState) {
        when (val state = updateState) {
            is UpdateState.Success -> {
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            is UpdateState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    EditProfileScreenContent(
        currentName = user?.displayName ?: "",
        currentPhotoUrl = user?.photoUrl,
        updateState = updateState,
        onBack = { navController.popBackStack() },
        onSave = { name, uri -> viewModel.saveProfile(name, uri) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(
    currentName: String,
    currentPhotoUrl: Uri?,
    updateState: UpdateState,
    onBack: () -> Unit,
    onSave: (String, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val alpha = remember { Animatable(0f) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(500))
    }

    PremiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .alpha(alpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                ) {
                    AsyncImage(
                        model = selectedImageUri ?: currentPhotoUrl ?: R.drawable.logo,
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                    )

                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = (-4).dp, y = (-4).dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+", color = Color.Black, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                AppButton(
                    text = "Save Changes",
                    onClick = { onSave(name, selectedImageUri) },
                    isLoading = updateState is UpdateState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun EditProfileScreenPreview() {
    THEORACLESPLATETheme {
        EditProfileScreenContent(
            currentName = "Gordon Ramsay",
            currentPhotoUrl = null,
            updateState = UpdateState.Idle,
            onBack = {},
            onSave = { _, _ -> }
        )
    }
}
