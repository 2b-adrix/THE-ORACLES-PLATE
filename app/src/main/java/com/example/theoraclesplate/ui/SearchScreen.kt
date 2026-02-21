package com.example.theoraclesplate.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.ui.cart.CartViewModel
import com.example.theoraclesplate.ui.search.SearchViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    rootNavController: NavController, 
    viewModel: SearchViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // Use transparent background
            .padding(16.dp)
    ) {
        Text(
            text = "Search",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White, // Changed to white
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Order to eat!", color = Color.White.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StartColor) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StartColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = StartColor
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
        
        Text(
            text = "Menu",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White, // Changed to white
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (state.isLoading) {
             Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else if (state.searchResults.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No results found", color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp)
            }
        } else {
            LazyColumn {
                itemsIndexed(state.searchResults) { index, food ->
                    val alpha = remember { Animatable(0f) }
                    LaunchedEffect(key1 = food) {
                        delay(index * 100L)
                        alpha.animateTo(1f, animationSpec = tween(500))
                    }
                     PopularFoodItem(
                         food = food, 
                         modifier = Modifier.alpha(alpha.value), 
                         onCardClick = { 
                            rootNavController.navigate("details/${food.id}/${food.sellerId}")
                         },
                        onAddClick = {
                            cartViewModel.addToCart(food)
                            scope.launch {
                                snackbarHostState.showSnackbar("${food.name} added to cart")
                            }
                        }
                    )
                }
            }
        }
    }
}
