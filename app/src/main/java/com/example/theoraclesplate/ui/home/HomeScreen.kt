package com.example.theoraclesplate.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.cart.CartViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rootNavController: NavController,
    onViewMenuClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val pagerState = rememberPagerState(pageCount = { state.banners.size })
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            if (pagerState.pageCount > 0 && !pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error?.let {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(text = it, color = Color.Red)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                // Banner Section
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) { page ->
                    AsyncImage(
                        model = state.banners.getOrNull(page),
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
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Items",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
            if (state.popularFood.isEmpty() && !state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No popular food items available.", color = Color.Gray)
                    }
                }
            } else {
                itemsIndexed(state.popularFood) { index, food ->
                    val alpha = remember { Animatable(0f) }
                    LaunchedEffect(key1 = food) {
                        delay(index * 150L)
                        alpha.animateTo(1f, animationSpec = tween(500))
                    }
                    PopularFoodItem(
                        food = food, 
                        modifier = Modifier.alpha(alpha.value),
                        onAddClick = { 
                            cartViewModel.addToCart(food)
                            scope.launch {
                                snackbarHostState.showSnackbar("${food.name} added to cart")
                            }
                        },
                        onCardClick = {
                            rootNavController.navigate("details/${food.id}/${food.sellerId}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PopularFoodItem(food: FoodItem, modifier: Modifier = Modifier, onCardClick: () -> Unit, onAddClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
            .clickable(onClick = onCardClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = food.imageUrl.ifEmpty { R.drawable.logo },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = food.name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${food.price}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StartColor
                )
            }

            Text(
                text = "ADD",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(StartColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable { onAddClick() }
            )
        }
    }
}
