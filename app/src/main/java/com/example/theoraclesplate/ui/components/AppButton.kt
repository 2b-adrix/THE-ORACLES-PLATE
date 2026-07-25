package com.example.theoraclesplate.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theoraclesplate.ui.theme.PrimaryGradient
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

enum class ButtonStyle {
    Filled, Outlined, Ghost, Gradient
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Filled,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color? = null,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "button_scale")

    val buttonModifier = modifier
        .heightIn(min = 54.dp)
        .scale(scale)

    if (style == ButtonStyle.Gradient) {
        Box(
            modifier = buttonModifier
                .clip(MaterialTheme.shapes.medium)
                .background(Brush.horizontalGradient(PrimaryGradient))
                .clickable(
                    enabled = enabled && !isLoading,
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            ButtonContent(text, isLoading, icon, color = Color.White)
        }
    } else {
        val buttonColors = when (style) {
            ButtonStyle.Filled -> ButtonDefaults.buttonColors(
                containerColor = containerColor ?: MaterialTheme.colorScheme.primary,
                contentColor = contentColor ?: MaterialTheme.colorScheme.onPrimary
            )
            ButtonStyle.Outlined -> ButtonDefaults.outlinedButtonColors(
                contentColor = contentColor ?: MaterialTheme.colorScheme.primary
            )
            ButtonStyle.Ghost -> ButtonDefaults.textButtonColors(
                contentColor = contentColor ?: MaterialTheme.colorScheme.primary
            )
            else -> ButtonDefaults.buttonColors()
        }

        when (style) {
            ButtonStyle.Filled -> {
                Button(
                    onClick = onClick,
                    modifier = buttonModifier,
                    enabled = enabled && !isLoading,
                    colors = buttonColors,
                    shape = MaterialTheme.shapes.medium,
                    interactionSource = interactionSource,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    ButtonContent(text, isLoading, icon)
                }
            }
            ButtonStyle.Outlined -> {
                OutlinedButton(
                    onClick = onClick,
                    modifier = buttonModifier,
                    enabled = enabled && !isLoading,
                    colors = buttonColors,
                    shape = MaterialTheme.shapes.medium,
                    interactionSource = interactionSource
                ) {
                    ButtonContent(text, isLoading, icon)
                }
            }
            ButtonStyle.Ghost -> {
                TextButton(
                    onClick = onClick,
                    modifier = buttonModifier,
                    enabled = enabled && !isLoading,
                    colors = buttonColors,
                    shape = MaterialTheme.shapes.medium,
                    interactionSource = interactionSource
                ) {
                    ButtonContent(text, isLoading, icon)
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun ButtonContent(
    text: String, 
    isLoading: Boolean, 
    icon: (@Composable () -> Unit)?,
    color: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = color,
                strokeWidth = 2.dp
            )
        } else {
            icon?.let {
                it()
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text, 
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    color = color
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F1E)
@Composable
fun AppButtonPreview() {
    THEORACLESPLATETheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppButton(text = "Gradient Button", style = ButtonStyle.Gradient, onClick = {})
            AppButton(text = "Filled Button", style = ButtonStyle.Filled, onClick = {})
            AppButton(text = "Outlined Button", style = ButtonStyle.Outlined, onClick = {})
            AppButton(text = "Loading Button", isLoading = true, onClick = {})
        }
    }
}
