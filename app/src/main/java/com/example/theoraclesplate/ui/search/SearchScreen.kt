package com.example.theoraclesplate.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.theoraclesplate.ui.PopularFoodItem

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = viewModel.state.value.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            label = { Text("Search") }
        )

        if (state.isLoading) {
            // Show a loading indicator
        } else if (state.error != null) {
            // Show an error message
        } else {
            LazyColumn {
                items(state.searchResults) { foodItem ->
                    PopularFoodItem(
                        food = foodItem,
                        onCardClick = { /* Handle card click */ },
                        onAddClick = { /* Handle add click */ }
                    )
                }
            }
        }
    }
}
