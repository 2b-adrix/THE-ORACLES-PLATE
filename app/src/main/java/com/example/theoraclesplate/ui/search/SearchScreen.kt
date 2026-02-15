package com.example.theoraclesplate.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.ui.PopularFoodItem
import com.example.theoraclesplate.ui.cart.CartViewModel

@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel(), cartViewModel: CartViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            label = { Text("Search") }
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error!!, color = Color.Red)
            }
        } else if (state.searchResults.isEmpty() && state.searchQuery.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No results found.")
            }
        } else {
            LazyColumn {
                items(state.searchResults) { foodItem ->
                    PopularFoodItem(
                        food = foodItem,
                        onCardClick = { navController.navigate("details/${foodItem.id}/${foodItem.sellerId}") },
                        onAddClick = { cartViewModel.addToCart(foodItem) }
                    )
                }
            }
        }
    }
}
