package com.example.theoraclesplate.ui.home.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.ui.cart.viewmodel.CartViewModel
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppCard
import com.example.theoraclesplate.ui.components.CardStyle
import com.example.theoraclesplate.ui.components.ShimmerBanner
import com.example.theoraclesplate.ui.components.ShimmerFoodItem
import com.example.theoraclesplate.ui.home.viewmodel.HomeState
import com.example.theoraclesplate.ui.home.viewmodel.HomeViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    rootNavController: NavController,
    onViewMenuClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    HomeScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onViewMenuClick = onViewMenuClick,
        onAddToCart = { food ->
            cartViewModel.addToCart(food)
            scope.launch {
                snackbarHostState.showSnackbar("${food.name} added to cart")
            }
        },
        onFoodClick = { food ->
            rootNavController.navigate("details/${food.id}/${food.sellerId}")
        },
        onProfileClick = { /* TODO */ }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    snackbarHostState: SnackbarHostState,
    onViewMenuClick: () -> Unit,
    onAddToCart: (FoodItem) -> Unit,
    onFoodClick: (FoodItem) -> Unit,
    onProfileClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val pagerState = rememberPagerState(pageCount = { if (isPreview && state.banners.isEmpty()) 1 else state.banners.size })
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Pizza", "Burger", "Sushi", "Desserts", "Drinks")

    if (!isPreview && state.banners.isNotEmpty()) {
        LaunchedEffect(pagerState) {
            while (true) {
                delay(3000)
                if (pagerState.pageCount > 0 && !pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    PremiumBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "Deliver to",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "Home - 123 Main St",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        IconButton(
                            onClick = onProfileClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Profile",
                                modifier = Modifier.size(28.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { 
                            Text(
                                "Search for food...", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.5f)
                            ) 
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Search, 
                                contentDescription = null, 
                                tint = Color.White.copy(alpha = 0.5f)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        readOnly = true,
                        enabled = false
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    if (state.isLoading && state.banners.isEmpty()) {
                        ShimmerBanner()
                    } else if (state.banners.isNotEmpty() || isPreview) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(16.dp)
                                .clip(MaterialTheme.shapes.large)
                        ) { page ->
                            if (isPreview && state.banners.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
                                    Text("Banner Placeholder", color = Color.White)
                                }
                            } else {
                                AsyncImage(
                                    model = state.banners.getOrNull(page),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.Black,
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    labelColor = Color.White.copy(alpha = 0.7f)
                                ),
                                border = null
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular Choices",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "See All",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onViewMenuClick() }
                        )
                    }
                }

                if (state.isLoading && state.popularFood.isEmpty()) {
                    items(5) {
                        ShimmerFoodItem()
                    }
                } else if (state.popularFood.isEmpty() && !isPreview) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Discovering delicious options...", 
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    val foodItems = if (isPreview && state.popularFood.isEmpty()) {
                        listOf(
                            FoodItem(id = "1", name = "Premium Burger", price = 15.0, description = "Juicy beef patty"),
                            FoodItem(id = "2", name = "Artisan Pizza", price = 22.0, description = "Wood-fired crust")
                        )
                    } else {
                        state.popularFood
                    }

                    itemsIndexed(foodItems) { index, food ->
                        val alpha = remember { Animatable(0f) }
                        LaunchedEffect(key1 = food) {
                            delay(index * 50L)
                            alpha.animateTo(1f, animationSpec = tween(300))
                        }
                        PopularFoodItem(
                            food = food, 
                            modifier = Modifier.alpha(alpha.value),
                            onAddClick = { onAddToCart(food) },
                            onCardClick = { onFoodClick(food) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PopularFoodItem(food: FoodItem, modifier: Modifier = Modifier, onCardClick: () -> Unit, onAddClick: () -> Unit) {
    AppCard(
        style = CardStyle.Glass,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onCardClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = food.imageUrl.ifEmpty { R.drawable.logo },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("4.5", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Fast Delivery • 20 min",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${food.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add to cart",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    THEORACLESPLATETheme {
        HomeScreenContent(
            state = HomeState(),
            snackbarHostState = remember { SnackbarHostState() },
            onViewMenuClick = {},
            onAddToCart = {},
            onFoodClick = {},
            onProfileClick = {}
        )
    }
}
