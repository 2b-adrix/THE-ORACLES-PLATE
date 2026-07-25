package com.example.theoraclesplate.ui.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.theoraclesplate.R
import com.example.theoraclesplate.ui.common.AnimatedCircleBackground
import com.example.theoraclesplate.ui.splash.viewmodel.SplashViewModel
import com.example.theoraclesplate.ui.theme.THEORACLESPLATETheme

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navigationRoute by viewModel.navigationRoute.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.checkUserStatus()
    }

    LaunchedEffect(navigationRoute) {
        navigationRoute?.let { route ->
            navController.navigate(route) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, delayMillis = 500)
        )
    }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, delayMillis = 500)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedCircleBackground(modifier = Modifier.fillMaxSize())

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(250.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    THEORACLESPLATETheme {
        SplashScreenContent()
    }
}
