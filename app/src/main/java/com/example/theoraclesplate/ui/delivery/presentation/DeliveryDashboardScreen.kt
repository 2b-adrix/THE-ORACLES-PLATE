package com.example.theoraclesplate.ui.delivery.presentation

import android.Manifest
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.delivery.viewmodel.DeliveryDashboardEvent
import com.example.theoraclesplate.ui.delivery.viewmodel.DeliveryDashboardState
import com.example.theoraclesplate.ui.delivery.viewmodel.DeliveryDashboardViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDashboardScreen(
    navController: NavController,
    viewModel: DeliveryDashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val mapView = remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DeliveryDashboardViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DeliveryDashboardScreenContent(
        state = state,
        onLogout = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("start") {
                popUpTo("delivery_dashboard") { inclusive = true }
            }
        },
        onNavigateToProfile = { navController.navigate("delivery_profile") },
        onUpdateOrderStatus = { orderId, status -> 
            viewModel.onEvent(DeliveryDashboardEvent.UpdateOrderStatus(orderId, status))
        },
        getCoordinatesForOrder = { order, onResult -> 
            viewModel.getCoordinatesForOrder(order, onResult)
        },
        onMyLocationClick = {
            try {
                locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        mapView.value?.controller?.animateTo(GeoPoint(location.latitude, location.longitude))
                    }
                }
            } catch (_: SecurityException) {}
        },
        mapView = mapView
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDashboardScreenContent(
    state: DeliveryDashboardState,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    getCoordinatesForOrder: (Order, (GeoPoint) -> Unit) -> Unit,
    onMyLocationClick: () -> Unit,
    mapView: MutableState<MapView?>
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ready for Pickup", "Out for Delivery", "Delivered")
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var isSheetVisible by remember { mutableStateOf(true) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                mapView.value?.let { 
                    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), it)
                    myLocationOverlay.enableMyLocation()
                    it.overlays.add(myLocationOverlay)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    val sheetOffsetY by animateDpAsState(
        targetValue = if (isSheetVisible) 0.dp else 400.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = ""
    )

    val isPreview = LocalInspectionMode.current

    if (!isPreview) {
        Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
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
            FloatingActionButton(
                onClick = onMyLocationClick,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = Color.Black)
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Map View Preview", color = Color.White)
                }
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(12.0)
                            mapView.value = this
                        }
                    },
                    update = { view ->
                        view.overlays.clear()
                        val orders = when (selectedTabIndex) {
                            0 -> state.readyForPickupOrders
                            1 -> state.outForDeliveryOrders
                            else -> state.deliveredOrders
                        }
                        orders.forEach { order ->
                            getCoordinatesForOrder(order) { geoPoint ->
                                val marker = Marker(view)
                                marker.position = geoPoint
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = order.userName
                                view.overlays.add(marker)
                            }
                        }
                        selectedOrder?.let { order ->
                            getCoordinatesForOrder(order) { geoPoint ->
                                view.controller.animateTo(geoPoint, 15.0, 1000L)
                            }
                        }
                        view.invalidate()
                    }
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = sheetOffsetY)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF0D0D1A).copy(alpha = 0.9f),
                                    Color(0xFF0D0D1A)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(top = 24.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = title, color = if(selectedTabIndex == index) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f)) }
                            )
                        }
                    }
                    when (selectedTabIndex) {
                        0 -> OrderList(orders = state.readyForPickupOrders, selectedOrder = selectedOrder, actionText = "Accept Order", onOrderClick = { selectedOrder = it }) {
                            onUpdateOrderStatus(it, "Out for Delivery")
                        }

                        1 -> OrderList(orders = state.outForDeliveryOrders, selectedOrder = selectedOrder, actionText = "Mark as Delivered", onOrderClick = { selectedOrder = it }) {
                            onUpdateOrderStatus(it, "Delivered")
                        }

                        2 -> OrderList(orders = state.deliveredOrders, selectedOrder = selectedOrder, actionText = null, onOrderClick = { selectedOrder = it }) {}
                    }
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun OrderList(orders: List<Order>, selectedOrder: Order?, actionText: String?, onOrderClick: (Order) -> Unit, onActionClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(orders) { order ->
            OrderCard(
                order = order,
                isSelected = order.orderId == selectedOrder?.orderId,
                actionText = actionText, 
                onOrderClick = { onOrderClick(order) }, 
                onActionClick = onActionClick
            )
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    isSelected: Boolean,
    actionText: String?,
    onOrderClick: () -> Unit,
    onActionClick: (String) -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onOrderClick() }
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Order #${order.orderId.take(6)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text(text = "$${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "User", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.userName, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Address", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.address, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            actionText?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onActionClick(order.orderId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black)
                ) {
                    Text(text = it, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview
@Composable
fun DeliveryDashboardScreenPreview() {
    THEORACLESPLATETheme {
        val mapView = remember { mutableStateOf<MapView?>(null) }
        DeliveryDashboardScreenContent(
            state = DeliveryDashboardState(
                readyForPickupOrders = listOf(
                    Order(orderId = "ORD123", userName = "John Doe", totalAmount = 25.50, address = "123 Street Name")
                ),
                isLoading = false
            ),
            onLogout = {},
            onNavigateToProfile = {},
            onUpdateOrderStatus = { _, _ -> },
            getCoordinatesForOrder = { _, _ -> },
            onMyLocationClick = {},
            mapView = mapView
        )
    }
}
