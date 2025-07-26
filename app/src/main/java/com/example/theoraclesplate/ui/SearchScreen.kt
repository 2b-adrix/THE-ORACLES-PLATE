package com.example.theoraclesplate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun SearchScreen(rootNavController: NavController) {
    var query by remember { mutableStateOf("") }
    val originalMenu = listOf(
        FoodItem("Pizza", "$5", R.drawable.food1),
        FoodItem("Burger", "$2", R.drawable.food2),
        FoodItem("Salad", "$6", R.drawable.food3),
        FoodItem("Pasta", "$5", R.drawable.food1),
        FoodItem("Sushi", "$2", R.drawable.food2)
    )
    
    val filteredMenu = if (query.isEmpty()) originalMenu else originalMenu.filter {
        it.name.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Search",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Order to eat!") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StartColor) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StartColor,
                unfocusedBorderColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
        
        Text(
            text = "Menu",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn {
            items(filteredMenu) { food ->
                 PopularFoodItem(food) {
                     rootNavController.navigate("details/${food.name}/${food.price.replace("$","")}/${food.image}")
                 }
            }
        }
    }
}
