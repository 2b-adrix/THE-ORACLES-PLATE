package com.example.theoraclesplate.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.theoraclesplate.R
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.model.Review
import com.example.theoraclesplate.ui.seller.menu.SellerMenuEvent
import com.example.theoraclesplate.ui.seller.menu.SellerMenuViewModel
import com.example.theoraclesplate.ui.seller.orders.SellerOrdersViewModel
import com.example.theoraclesplate.ui.seller.reviews.ReviewViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    navController: NavController,
    menuViewModel: SellerMenuViewModel = hiltViewModel(),
    ordersViewModel: SellerOrdersViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val menuState = menuViewModel.state.value
    val ordersState by ordersViewModel.orders.collectAsState()
    val todaysRevenue by ordersViewModel.todaysRevenue.collectAsState()
    val todaysOrders by ordersViewModel.todaysOrders.collectAsState()
    val weeklySales by ordersViewModel.weeklySales.collectAsState()
    val reviews by reviewViewModel.reviews.collectAsState()
    val user = Firebase.auth.currentUser
    var selectedTabIndex by remember { mutableStateOf(0) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0D1A),
            Color(0xFF1A1A2E)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller Dashboard", color = Color.White) },
                actions = {
                    IconButton(onClick = { navController.navigate("seller_profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                    }
                    IconButton(onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("start") {
                            popUpTo("seller_dashboard") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = StartColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 1) { // Show FAB only on the Menu tab
                FloatingActionButton(
                    onClick = { navController.navigate("add_menu_item") },
                    containerColor = StartColor,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Menu Item", tint = Color.Black)
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                // Welcome Message
                Text(
                    text = "Welcome, ${user?.displayName ?: "Seller"}!",
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

                // Dashboard Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DashboardMetricCard(
                        title = "Today's Revenue",
                        value = "$${String.format("%.2f", todaysRevenue)}",
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    DashboardMetricCard(
                        title = "Today's Orders",
                        value = todaysOrders.toString(),
                        icon = Icons.Default.ShoppingBasket,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    DashboardMetricCard(
                        title = "Total Items",
                        value = menuState.menuItems.size.toString(),
                        icon = Icons.Default.RestaurantMenu,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sales Chart
                SalesChart(salesData = weeklySales)

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tabs for Live Orders and Menu
            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent, contentColor = StartColor) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                    Text("Live Orders", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                    Text("Menu", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                    Text("Reviews", modifier = Modifier.padding(16.dp))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        items(ordersState) { order ->
                            OrderCard(order = order, onUpdateStatus = ordersViewModel::updateOrderStatus)
                        }
                    }
                    1 -> {
                        itemsIndexed(menuState.menuItems) { index, (id, item) ->
                            val alpha = remember { Animatable(0f) }
                            LaunchedEffect(key1 = item) {
                                delay(index * 100L)
                                alpha.animateTo(1f, animationSpec = tween(500))
                            }
                            MenuItemCard(
                                item = item,
                                modifier = Modifier.alpha(alpha.value),
                                onEditClick = {
                                    navController.navigate("edit_menu_item/$id")
                                },
                                onDeleteClick = {
                                    menuViewModel.onEvent(SellerMenuEvent.DeleteMenuItem(id))
                                }
                            )
                        }
                    }
                    2 -> {
                        item {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
                                Text("Average Rating: ${String.format("%.1f", averageRating)}/5", style = MaterialTheme.typography.headlineSmall, color = Color.White)
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
                RatingBar(rating = review.rating)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun OrderCard(order: Order, onUpdateStatus: (String, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order from ${order.userName}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("Total: $${order.totalAmount}", color = StartColor)
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach {
                Text("- ${it.name} (x${it.quantity})", color = Color.White.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onUpdateStatus(order.orderId, "Preparing") },
                    enabled = order.status == "Pending",
                    colors = ButtonDefaults.buttonColors(containerColor = StartColor, contentColor = Color.Black)
                ) {
                    Text("Accept")
                }
                Button(
                    onClick = { onUpdateStatus(order.orderId, "Ready") },
                    enabled = order.status == "Preparing",
                    colors = ButtonDefaults.buttonColors(containerColor = StartColor, contentColor = Color.Black)
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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = StartColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
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
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl.ifEmpty { R.drawable.logo },
                contentDescription = "Food Item Image",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$${item.price}", color = StartColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.2f)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Item", tint = Color.White.copy(alpha = 0.8f))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = StartColor.copy(alpha = 0.8f))
                }
            }
        }
    }
}
