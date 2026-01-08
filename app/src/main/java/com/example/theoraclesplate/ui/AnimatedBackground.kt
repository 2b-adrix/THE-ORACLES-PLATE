package com.example.theoraclesplate.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.example.theoraclesplate.ui.theme.StartColor
import kotlin.random.Random

// Data class to hold the state of each moving circle
data class CircleParticle(
    var x: Float,
    var y: Float,
    var dx: Float, // x-velocity
    var dy: Float, // y-velocity
    val radius: Float,
    val color: Color
)

@Composable
fun AnimatedCircleBackground(modifier: Modifier = Modifier) {
    val particles = remember { mutableStateListOf<CircleParticle>() }
    val density = LocalDensity.current.density
    val context = LocalContext.current

    // Initialize particles once
    LaunchedEffect(Unit) {
        val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()
        val particleColors = listOf(
            StartColor,
            Color(0xFF8E24AA), // Purple
            Color(0xFF03A9F4), // Light Blue
            Color(0xFFF06292)  // Pink
        )
        repeat(20) { // Create 20 circles
            particles.add(
                CircleParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    dx = (Random.nextFloat() - 0.5f) * 2 * density, // Random velocity
                    dy = (Random.nextFloat() - 0.5f) * 2 * density,
                    radius = Random.nextInt(15, 60).toFloat() * density,
                    color = particleColors.random()
                )
            )
        }
    }

    // The animation ticker
    val infiniteTransition = rememberInfiniteTransition(label = "circle_anim_bg")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f, // We just need it to tick
        animationSpec = infiniteRepeatable(
            animation = tween(16, easing = LinearEasing), // Approx 60 FPS
            repeatMode = RepeatMode.Restart
        ),
        label = "time_bg"
    )

    Canvas(modifier = modifier) {
        // Dummy dependency on `time` to trigger redraws
        val currentTime = time

        particles.forEach { particle ->
            // Update position
            particle.x += particle.dx
            particle.y += particle.dy

            // Bounce off screen edges
            if (particle.x < -particle.radius || particle.x > size.width + particle.radius) particle.dx *= -1
            if (particle.y < -particle.radius || particle.y > size.height + particle.radius) particle.dy *= -1

            // Draw the glowing circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(particle.color.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(particle.x, particle.y),
                    radius = particle.radius
                ),
                center = Offset(particle.x, particle.y),
                radius = particle.radius
            )
        }
    }
}
