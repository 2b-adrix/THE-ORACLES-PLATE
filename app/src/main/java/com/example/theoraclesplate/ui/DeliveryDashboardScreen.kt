package com.example.theoraclesplate.ui

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.model.Order
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardEvent
import com.example.theoraclesplate.ui.delivery.DeliveryDashboardViewModel
import com.example.theoraclesplate.ui.theme.StartColor
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ready for Pickup", "Out for Delivery", "Delivered")
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    val mapView = remember { mutableStateOf<MapView?>(null) }
    var isSheetVisible by remember { mutableStateOf(false) }
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // Permission granted, show my location
                mapView.value?.let { 
                    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), it)
                    myLocationOverlay.enableMyLocation()
                    it.overlays.add(myLocationOverlay)
                }
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        isSheetVisible = true
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    val sheetOffsetY by animateDpAsState(
        targetValue = if (isSheetVisible) 0.dp else 400.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = ""
    )

    Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DeliveryDashboardViewModel.UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery Dashboard", color = Color.White) },
                actions = {
                    IconButton(onClick = { navController.navigate("delivery_profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                    }
                    IconButton(onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("start") {
                            popUpTo("delivery_dashboard") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = StartColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                     try {
                        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                mapView.value?.controller?.animateTo(GeoPoint(location.latitude, location.longitude))
                            }
                        }
                    } catch (_: SecurityException) {
                        // this should not happen as we are requesting permissions
                    }
                },
                shape = CircleShape,
                containerColor = StartColor
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = Color.Black)
            }
        },
        containerColor = Color(0xFF0D0D1A)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                        viewModel.getCoordinatesForOrder(order) { geoPoint ->
                            val marker = Marker(view)
                            marker.position = geoPoint
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            marker.title = order.userName
                            view.overlays.add(marker)
                        }
                    }
                    selectedOrder?.let { order ->
                        viewModel.getCoordinatesForOrder(order) { geoPoint ->
                            view.controller.animateTo(geoPoint, 15.0, 1000L)
                        }
                    }
                    view.invalidate()
                }
            )

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
                                    Color(0xFF0D0D1A).copy(alpha = 0.8f),
                                    Color(0xFF0D0D1A)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(top = 24.dp)
                ) {
                    TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent, contentColor = StartColor) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = title) }
                            )
                        }
                    }
                    when (selectedTabIndex) {
                        0 -> OrderList(orders = state.readyForPickupOrders, selectedOrder = selectedOrder, actionText = "Accept Order", onOrderClick = { selectedOrder = it }) {
                            viewModel.onEvent(DeliveryDashboardEvent.UpdateOrderStatus(it, "Out for Delivery"))
                        }

                        1 -> OrderList(orders = state.outForDeliveryOrders, selectedOrder = selectedOrder, actionText = "Mark as Delivered", onOrderClick = { selectedOrder = it }) {
                            viewModel.onEvent(DeliveryDashboardEvent.UpdateOrderStatus(it, "Delivered"))
                        }

                        2 -> OrderList(orders = state.deliveredOrders, selectedOrder = selectedOrder, actionText = null, onOrderClick = { selectedOrder = it }) {}
                    }
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = StartColor)
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
    val borderColor = if (isSelected) StartColor else Color.Transparent
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
                Text(text = "$${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StartColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "User", tint = StartColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.userName, color = Color.White.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Address", tint = StartColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.address, color = Color.White.copy(alpha = 0.8f))
            }
            actionText?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onActionClick(order.orderId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StartColor)
                ) {
                    Text(text = it, color = Color.Black)
                }
            }
        }
    }
}
