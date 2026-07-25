package com.example.theoraclesplate.ui.search.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.cart.viewmodel.CartViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.home.presentation.PopularFoodItem
import com.example.theoraclesplate.ui.search.viewmodel.SearchState
import com.example.theoraclesplate.ui.search.viewmodel.SearchViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    rootNavController: NavController, 
    viewModel: SearchViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SearchScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
        onFoodClick = { food ->
            rootNavController.navigate("details/${food.id}/${food.sellerId}")
        },
        onAddToCart = { food ->
            cartViewModel.addToCart(food)
            scope.launch {
                snackbarHostState.showSnackbar("${food.name} added to cart")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    state: SearchState,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChange: (String) -> Unit,
    onFoodClick: (FoodItem) -> Unit,
    onAddToCart: (FoodItem) -> Unit
) {
    PremiumBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Order to eat!", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Results",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (state.isLoading) {
                     Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (state.searchResults.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (state.searchQuery.isEmpty()) "Start typing to find food" else "No results found", 
                            color = Color.White.copy(alpha = 0.5f), 
                            fontSize = 18.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        itemsIndexed(state.searchResults) { index, food ->
                            val alpha = remember { Animatable(0f) }
                            LaunchedEffect(key1 = food) {
                                delay(index * 50L)
                                alpha.animateTo(1f, animationSpec = tween(300))
                            }
                             PopularFoodItem(
                                 food = food, 
                                 modifier = Modifier.alpha(alpha.value), 
                                 onCardClick = { onFoodClick(food) },
                                 onAddClick = { onAddToCart(food) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    THEORACLESPLATETheme {
        SearchScreenContent(
            state = SearchState(
                searchQuery = "Burger",
                searchResults = listOf(
                    FoodItem(id = "1", name = "Cheese Burger", price = 12.0),
                    FoodItem(id = "2", name = "Veggie Burger", price = 10.0)
                ),
                isLoading = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onSearchQueryChange = {},
            onFoodClick = {},
            onAddToCart = {}
        )
    }
}
