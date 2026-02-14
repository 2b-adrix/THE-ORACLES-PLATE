package com.example.theoraclesplate.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun RatingBar(modifier: Modifier = Modifier, rating: Float) {
    Row(modifier = modifier) {
        val fullStars = rating.toInt()
        val halfStar = if (rating - fullStars >= 0.5f) 1 else 0
        val emptyStars = 5 - fullStars - halfStar

        repeat(fullStars) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = StartColor)
        }
        if (halfStar == 1) {
            Icon(Icons.Filled.StarHalf, contentDescription = null, tint = StartColor)
        }
        repeat(emptyStars) {
            Icon(Icons.Filled.StarOutline, contentDescription = null, tint = StartColor)
        }
    }
}
