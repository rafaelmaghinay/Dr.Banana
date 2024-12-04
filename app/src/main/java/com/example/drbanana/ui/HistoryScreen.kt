package com.example.drbanana.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drbanana.ui.theme.DrBananaTheme

@Composable
fun HistoryScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color(0xFFD0FFCF), // Start color (white)
                        0.6f to Color(0xFFD0FFCF),// Duplicate the first color to stop at 60%
                        1f to Color(0xFFFFFFFF)  // End color (green)
                    ),
                    start = Offset(0f, 0f),                  // Top
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Text(
            text = "Your Diagnoses",
            fontWeight = Bold,
            color = Color.Black,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    DrBananaTheme {
        HistoryScreen()
    }
}