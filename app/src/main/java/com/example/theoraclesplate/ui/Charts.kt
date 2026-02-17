package com.example.theoraclesplate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.theoraclesplate.ui.theme.StartColor

@Composable
fun SimpleBarChart(data: Map<String, Int>, modifier: Modifier = Modifier) {
    val maxValue = data.values.maxOrNull() ?: 1

    Row(
        modifier = modifier.fillMaxWidth().height(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .fillMaxHeight(fraction = value.toFloat() / maxValue.toFloat())
                        .background(StartColor)
                )
                Text(text = label, color = Color.White)
            }
        }
    }
}
