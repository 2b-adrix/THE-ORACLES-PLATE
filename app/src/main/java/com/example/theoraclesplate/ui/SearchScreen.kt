package com.example.theoraclesplate.ui

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun SearchScreen(rootNavController: NavController) {
    var query by remember { mutableStateOf("") }
    val database = Firebase.database
    val context = LocalContext.current
    val menuItems = remember { mutableStateListOf<FoodItem>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val menuRef = database.reference.child("menu")
        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(FoodItem::class.java)
                    if (item != null) {
                        menuItems.add(item)
                    }
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load menu", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        })
    }
    
    val filteredMenu = if (query.isEmpty()) menuItems else menuItems.filter {
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

        if (isLoading) {
             Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = StartColor)
            }
        } else {
            LazyColumn {
                items(filteredMenu) { food ->
                     PopularFoodItem(food) {
                         val encodedImage = Uri.encode(food.image)
                         rootNavController.navigate("details/${food.name}/${food.price.replace("$","")}/?image=${encodedImage}")
                     }
                }
            }
        }
    }
}
