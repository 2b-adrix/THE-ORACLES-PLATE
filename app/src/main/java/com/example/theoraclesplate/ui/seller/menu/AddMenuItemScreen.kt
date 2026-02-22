package com.example.theoraclesplate.ui.seller.menu

import android.net.Uri
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.ui.theme.StartColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuItemScreen(navController: NavController, viewModel: SellerMenuViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        imageUri = uri
    }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        alpha.animateTo(1f, animationSpec = tween(500))
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SellerMenuViewModel.UiEvent.NavigateUp -> {
                    navController.popBackStack()
                }
                is SellerMenuViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a New Dish") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF1A1A2E) // Consistent background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .alpha(alpha.value)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .align(Alignment.CenterHorizontally)
                    .clickable { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(50.dp))
                } else {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StartColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                cursorColor = StartColor,
                focusedTextColor = Color.White, 
                unfocusedTextColor = Color.White
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(SellerMenuEvent.EnteredName(it)) },
                label = { Text("Dish Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(SellerMenuEvent.EnteredDescription(it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.price,
                onValueChange = { viewModel.onEvent(SellerMenuEvent.EnteredPrice(it)) },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { 
                    viewModel.onEvent(SellerMenuEvent.AddMenuItem(imageUri))
                 },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isAddingItem,
                colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isAddingItem) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Add Dish", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}