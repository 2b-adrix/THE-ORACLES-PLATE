package com.example.theoraclesplate.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(rootNavController: NavController, onViewMenuClick: () -> Unit) {
    val banners = listOf(R.drawable.banner1, R.drawable.banner2)
    val popularFood = remember { mutableStateListOf<FoodItem>() }
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val database = Firebase.database
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val menuRef = database.reference.child("menu")
        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                popularFood.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(FoodItem::class.java)
                    if (item != null) {
                        popularFood.add(item)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Menu Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // Make background transparent
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            // Banner Section
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) { page ->
                Image(
                    painter = painterResource(id = banners[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        item {
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Items",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White // Changed to white
                )
                
                Text(
                    text = "View Menu",
                    color = StartColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { 
                         onViewMenuClick()
                    }
                )
            }
        }

        items(popularFood) { food ->
            PopularFoodItem(food) {
                val encodedImage = Uri.encode(food.image)
                rootNavController.navigate("details/${food.name}/${food.price.replace("$","")}/?image=${encodedImage}")
            }
        }
    }
}

@Composable
fun PopularFoodItem(food: FoodItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f) // Glassmorphism effect
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = food.image.ifEmpty { R.drawable.food1 },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = food.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White // Changed to white
                )
                Text(
                    text = food.price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f) // Changed to light gray
                )
            }
            
            Text(
                text = "ADD",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(StartColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onClick() }
            )
        }
    }
}
