package com.example.theoraclesplate.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.preference.PreferenceManager

@Composable
fun DeliveryDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    // Initialize OsmDroid configuration
    Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                    setBuiltInZoomControls(true)
                    setMultiTouchControls(true)
                    controller.setZoom(10.0)
                    controller.setCenter(GeoPoint(1.35, 103.87)) // Default center (Singapore)
                }
            },
            update = { mapView ->
                // This block will be re-executed when the state changes.
                // For now, no dynamic updates are needed directly from Compose state.
                // You can add markers here dynamically if needed based on Compose state.
            }
        )
    }
}
