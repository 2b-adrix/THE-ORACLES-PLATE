package com.example.theoraclesplate.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.theoraclesplate.ui.theme.BackgroundDark
import com.example.theoraclesplate.ui.theme.DarkGradient

@Composable
fun PremiumBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(DarkGradient))
    ) {
        AnimatedCircleBackground(modifier = Modifier.fillMaxSize())
        
        // Subtle overlay for better text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, BackgroundDark.copy(alpha = 0.8f)),
                        center = androidx.compose.ui.geometry.Offset.Unspecified,
                        radius = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        content()
    }
}
