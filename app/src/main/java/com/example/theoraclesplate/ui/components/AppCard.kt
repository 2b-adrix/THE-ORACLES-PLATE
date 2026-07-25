package com.example.theoraclesplate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.theoraclesplate.ui.theme.GlassBorder
import com.example.theoraclesplate.ui.theme.GlassWhite
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

enum class CardStyle {
    Default, Glass
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    style: CardStyle = CardStyle.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (style == CardStyle.Glass) {
        Box(
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
                .background(GlassWhite)
                .border(1.dp, GlassBorder, MaterialTheme.shapes.large)
                .then(
                    if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
                )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier
                .then(
                    if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
                ),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F1E)
@Composable
fun AppCardPreview() {
    THEORACLESPLATETheme {
        Column(modifier = Modifier.padding(20.dp)) {
            AppCard(style = CardStyle.Glass) {
                Text("Glass Card Content", color = Color.White)
                Text("Sophisticated and modern", color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}
