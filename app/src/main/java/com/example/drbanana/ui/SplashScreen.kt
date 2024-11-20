package com.example.drbanana.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drbanana.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    // State to trigger animations
    val startTextAnimation = remember { mutableStateOf(false) }
    val startImageAnimation = remember { mutableStateOf(false) }
    val startFadeAnimation = remember { mutableStateOf(false) }

    // Animations for text fade-in
    val textAlphaAnimation = animateFloatAsState(
        targetValue = if (startTextAnimation.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), // 1 second fade-in
        label = "Text fade-in"
    )

    // Animations for image fade-in and scale-up
    val imageAlphaAnimation = animateFloatAsState(
        targetValue = if (startImageAnimation.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), // 1 second fade-in
        label = "Image fade-in"
    )
    val imageScaleAnimation = animateFloatAsState(
        targetValue = if (startImageAnimation.value) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000), // 1 second scale-up
        label = "Image scale-up"
    )

    // Animations for fade-out
    val fadeOutAnimation = animateFloatAsState(
        targetValue = if (startFadeAnimation.value) 0f else 1f,
        animationSpec = tween(durationMillis = 1000), // 1 second fade-out
        label = "Fade-out"
    )

    // Start animation when this composable appears
    LaunchedEffect(Unit) {
        startTextAnimation.value = true
        delay(1000)
        startImageAnimation.value = true
        delay(2000)
        startFadeAnimation.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF), // Start color (white)
                        Color(0xFF61F878)  // End color (green)
                    ),
                    start = Offset(0f, 0f),                  // Top
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),  // Add padding around the column if needed
            verticalArrangement = Arrangement.Center, // Center vertically within the parent
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally within the column
        ) {
            Image(
                painter = painterResource(id = R.drawable.bgremovedlogo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(176.dp, 235.dp)
                    .graphicsLayer(
                        alpha = imageAlphaAnimation.value * fadeOutAnimation.value,
                        scaleX = imageScaleAnimation.value,
                        scaleY = imageScaleAnimation.value
                    )
            )

            Text(
                text = "Dr.Banana",
                fontSize = 60.sp,                // Set the font size to 24sp
                fontWeight = FontWeight.Bold,     // Make the text bold
                color = Color.Black, // Set the text color to black
                modifier = Modifier
                    .padding(16.dp)
                    .graphicsLayer(
                        alpha = textAlphaAnimation.value * fadeOutAnimation.value)
            )
        }
    }
}