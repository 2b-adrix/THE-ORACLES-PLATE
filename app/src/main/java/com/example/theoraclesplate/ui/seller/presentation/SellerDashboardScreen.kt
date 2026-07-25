package com.example.theoraclesplate.ui.seller.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.Review
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.common.RatingBar
import com.example.theoraclesplate.ui.seller.viewmodel.*
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SellerDashboardScreen(
    navController: NavController,
    menuViewModel: SellerMenuViewModel = hiltViewModel(),
    ordersViewModel: SellerOrdersViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val menuState by menuViewModel.state
    val ordersState by ordersViewModel.state.collectAsState()
    val reviews by reviewViewModel.reviews.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser

    SellerDashboardScreenContent(
        userName = user?.displayName ?: "Seller",
        menuState = menuState,
        ordersState = ordersState,
        reviews = reviews,
        onLogout = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("start") {
                popUpTo("seller_dashboard") { inclusive = true }
            }
        },
        onNavigateToProfile = { navController.navigate("seller_profile") },
        onNavigateToAddItem = { navController.navigate("add_menu_item") },
        onNavigateToEditItem = { itemId -> navController.navigate("edit_menu_item/$itemId") },
        onDeleteMenuItem = { itemId -> menuViewModel.onEvent(SellerMenuEvent.DeleteMenuItem(itemId)) },
        onUpdateOrderStatus = { ordersViewModel.onEvent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreenContent(
    userName: String,
    menuState: SellerMenuState,
    ordersState: SellerOrdersState,
    reviews: List<Review>,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    onNavigateToEditItem: (String) -> Unit,
    onDeleteMenuItem: (String) -> Unit,
    onUpdateOrderStatus: (SellerOrdersEvent) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    PremiumBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Seller Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                if (selectedTabIndex == 1) {
                    FloatingActionButton(
                        onClick = onNavigateToAddItem,
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Menu Item", tint = Color.Black)
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Welcome, $userName!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Here's your performance summary.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardMetricCard(
                            title = "Today's Revenue",
                            value = "$${String.format("%.2f", ordersState.orders.filter { it.timestamp > System.currentTimeMillis() - 86400000 }.sumOf { it.totalAmount })}",
                            icon = Icons.Default.MonetizationOn,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Today's Orders",
                            value = ordersState.orders.filter { it.timestamp > System.currentTimeMillis() - 86400000 }.size.toString(),
                            icon = Icons.Default.ShoppingBasket,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Total Items",
                            value = menuState.menuItems.size.toString(),
                            icon = Icons.Default.RestaurantMenu,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                        Text("Live Orders", modifier = Modifier.padding(16.dp), color = if(selectedTabIndex == 0) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f))
                    }
                    Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                        Text("Menu", modifier = Modifier.padding(16.dp), color = if(selectedTabIndex == 1) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f))
                    }
                    Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                        Text("Reviews", modifier = Modifier.padding(16.dp), color = if(selectedTabIndex == 2) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f))
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> {
                            items(ordersState.orders) { order ->
                                OrderCard(order = order, onUpdateStatus = onUpdateOrderStatus)
                            }
                        }
                        1 -> {
                            itemsIndexed(menuState.menuItems) { index, item ->
                                val alpha = remember { Animatable(0f) }
                                LaunchedEffect(key1 = item) {
                                    delay(index * 50L)
                                    alpha.animateTo(1f, animationSpec = tween(300))
                                }
                                MenuItemCard(
                                    item = item,
                                    modifier = Modifier.alpha(alpha.value),
                                    onEditClick = { onNavigateToEditItem(item.id) },
                                    onDeleteClick = { onDeleteMenuItem(item.id) }
                                )
                            }
                        }
                        2 -> {
                            item {
                                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                    val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
                                    Text("Average Rating: ${String.format("%.1f", averageRating)}/5", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                            items(reviews) { review ->
                                ReviewCard(review = review)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.userName, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                RatingBar(rating = review.rating.toFloat())
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun OrderCard(order: Order, onUpdateStatus: (SellerOrdersEvent) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order from ${order.userName}", fontWeight = FontWeight.Bold, color = Color.White)
                Text("Status: ${order.status}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
            }
            Text("Total: $${order.totalAmount}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach {
                Text("- ${it.name} (x${it.quantity})", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onUpdateStatus(SellerOrdersEvent.UpdateOrderStatus(order.orderId, "Preparing")) },
                    enabled = order.status == "Pending",
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black)
                ) {
                    Text("Accept")
                }
                Button(
                    onClick = { onUpdateStatus(SellerOrdersEvent.UpdateOrderStatus(order.orderId, "Ready")) },
                    enabled = order.status == "Preparing",
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.Black)
                ) {
                    Text("Ready")
                }
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun MenuItemCard(
    item: FoodItem,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl.ifEmpty { R.drawable.logo },
                contentDescription = "Food Item Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$${item.price}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Column(verticalArrangement = Arrangement.Center) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Item", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun SellerDashboardScreenPreview() {
    THEORACLESPLATETheme {
        SellerDashboardScreenContent(
            userName = "Gordon Ramsay",
            menuState = SellerMenuState(
                menuItems = listOf(
                    FoodItem(name = "Beef Wellington", price = 45.0, description = "Classic signature dish"),
                    FoodItem(name = "Risotto", price = 25.0, description = "Creamy and delicious")
                )
            ),
            ordersState = SellerOrdersState(
                orders = listOf(
                    Order(orderId = "1", userName = "John Doe", totalAmount = 70.0, status = "Pending")
                )
            ),
            reviews = listOf(
                Review(userName = "Critique", rating = 5f, comment = "Perfect!")
            ),
            onLogout = {},
            onNavigateToProfile = {},
            onNavigateToAddItem = {},
            onNavigateToEditItem = {},
            onDeleteMenuItem = {},
            onUpdateOrderStatus = {}
        )
    }
}
