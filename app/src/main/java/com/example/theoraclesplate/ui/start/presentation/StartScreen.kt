package com.example.theoraclesplate.ui.start.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.common.PremiumBackground
import com.example.theoraclesplate.ui.components.AppButton
import com.example.theoraclesplate.ui.components.ButtonStyle
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StartScreen(navController: NavController) {
    StartScreenContent(
        onNavigateToBuyer = { navController.navigate("login_screen") },
        onNavigateToSeller = { navController.navigate("seller_login") },
        onNavigateToDelivery = { navController.navigate("delivery_login") },
        onNavigateToAdmin = { navController.navigate("admin_login") }
    )
}

@Composable
fun StartScreenContent(
    onNavigateToBuyer: () -> Unit,
    onNavigateToSeller: () -> Unit,
    onNavigateToDelivery: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val alpha1 = remember { Animatable(0f) }
    val alpha2 = remember { Animatable(0f) }
    val buttonsAlpha = remember { Animatable(0f) }

    // Floating animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "logo_float")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y_offset"
    )

    LaunchedEffect(key1 = true) {
        launch {
            delay(300)
            alpha1.animateTo(1f, animationSpec = tween(1000))
        }
        launch {
            delay(600)
            alpha2.animateTo(1f, animationSpec = tween(1000))
        }
        launch {
            delay(1200)
            buttonsAlpha.animateTo(1f, animationSpec = tween(1200))
        }
    }

    PremiumBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1.5f))

            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(200.dp)
                    .alpha(alpha1.value)
                    .graphicsLayer {
                        translationY = floatAnim
                    }
            )

            Text(
                text = "The Oracle\'s Plate",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                modifier = Modifier.alpha(alpha1.value)
            )

            Text(
                text = "Savor the extraordinary",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .alpha(alpha2.value)
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonsAlpha.value),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppButton(
                    text = "Continue as Buyer",
                    onClick = onNavigateToBuyer,
                    style = ButtonStyle.Gradient,
                    modifier = Modifier.fillMaxWidth()
                )

                AppButton(
                    text = "Seller Portal",
                    onClick = onNavigateToSeller,
                    style = ButtonStyle.Outlined,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                AppButton(
                    text = "Delivery Agent",
                    onClick = onNavigateToDelivery,
                    style = ButtonStyle.Outlined,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AppButton(
                text = "Admin Dashboard",
                onClick = onNavigateToAdmin,
                style = ButtonStyle.Ghost,
                contentColor = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    THEORACLESPLATETheme {
        StartScreenContent({}, {}, {}, {})
    }
}
