package com.example.theoraclesplate.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun SalesChart(salesData: Map<String, Double>) {
    val maxValue = salesData.values.maxOrNull() ?: 0.0

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sales This Week", style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        if (salesData.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                salesData.entries.forEach { (day, amount) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Canvas(modifier = Modifier.height(100.dp).width(20.dp)) {
                            val barHeight = if (maxValue > 0) (amount / maxValue * size.height).toFloat() else 0f
                            drawLine(
                                color = StartColor,
                                start = Offset(x = center.x, y = size.height),
                                end = Offset(x = center.x, y = size.height - barHeight),
                                strokeWidth = 20f
                            )
                        }
                        Text(text = day, style = MaterialTheme.typography.bodySmall, color = Color.White, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            Text("No sales data available for this week.", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
