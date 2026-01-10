package com.example.theoraclesplate.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.theoraclesplate.ui.details.DetailsEvent
import com.example.theoraclesplate.ui.details.DetailsViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DetailsViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) { // Themed for dark mode

        // AnimatedCircleBackground(modifier = Modifier.fillMaxSize())

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else if (state.foodItem != null) {
            val foodItem = state.foodItem
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = StartColor
                    )
                ) {
                    Text("< Back", fontSize = 18.sp)
                }

                Text(
                    text = foodItem.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White, // Themed for dark mode
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                AsyncImage(
                    model = foodItem.image.ifEmpty { "https://picsum.photos/200/300" }, // Fallback image
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Text(
                    text = "Price: ${foodItem.price}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = StartColor,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = "Description",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White, // Themed for dark mode
                    modifier = Modifier.padding(top = 24.dp)
                )

                Text(
                    text = foodItem.description,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f), // Themed for dark mode
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.onEvent(DetailsEvent.AddToCart(foodItem)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StartColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add To Cart", fontSize = 18.sp, color = Color.White)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Food item not found", color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}
