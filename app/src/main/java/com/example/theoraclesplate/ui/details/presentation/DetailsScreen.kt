package com.example.theoraclesplate.ui.details.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppButton
import com.example.theoraclesplate.ui.details.viewmodel.DetailsEvent
import com.example.theoraclesplate.ui.details.viewmodel.DetailsState
import com.example.theoraclesplate.ui.details.viewmodel.DetailsViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DetailsViewModel.UiEvent.ShowToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    DetailsScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onAddToCart = { viewModel.onEvent(DetailsEvent.AddToCart(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenContent(
    state: DetailsState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onAddToCart: (FoodItem) -> Unit
) {
    PremiumBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Product Details", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) { 

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.foodItem != null) {
                    val foodItem = state.foodItem
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .shadow(16.dp, RoundedCornerShape(24.dp))
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                        ) {
                            AsyncImage(
                                model = foodItem.imageUrl.ifEmpty { "https://picsum.photos/400/600" },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Floating Rating Badge
                            Surface(
                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("4.8", fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = foodItem.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Text(
                                text = "$${foodItem.price}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = foodItem.description.ifEmpty { "Enjoy this delicious and carefully prepared dish, made with the finest ingredients to satisfy your culinary cravings." },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AppButton(
                            text = "Add to Cart",
                            onClick = { onAddToCart(foodItem) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Food item not found", color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DetailsScreenPreview() {
    THEORACLESPLATETheme {
        DetailsScreenContent(
            state = DetailsState(
                foodItem = FoodItem(
                    id = "1",
                    name = "Truffle Pasta",
                    price = 32.50,
                    description = "Hand-made tagliatelle with fresh black truffle and parmesan cream sauce."
                ),
                isLoading = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onAddToCart = {}
        )
    }
}
