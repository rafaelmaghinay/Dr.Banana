package com.example.drbanana.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.drbanana.R

@Composable
fun HomeScreen(navController: NavHostController) {
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
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Got a banana problem? Let’s figure it out together!",
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(top = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp)
                    .size(width = 200.dp, height = 227.dp)
                    .background(color = Color.White)
                    .align(Alignment.CenterHorizontally)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.splitleaf),
                                contentDescription = "split leaf",
                                Modifier.size(67.dp, 82.dp),
                                alignment = Alignment.Center
                            )
                            Text(
                                text = "Stand next to your Leaf",
                                fontSize = 8.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.size(67.dp, 40.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.scanner),
                                contentDescription = "Scanner",
                                Modifier.size(85.dp),
                                alignment = Alignment.Center
                            )
                            Text(
                                text = "Scan Your Leaf",
                                fontSize = 8.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.diagnosis),
                                contentDescription = "diagnosis",
                                Modifier.size(92.dp, 90.dp)
                            )
                            Text(
                                text = "See Diagnosis",
                                fontSize = 8.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    ScanButton(onImageCaptured = {}, navController = navController)
                }
            }

            Text(
                text = "Simply Tap the Scan Button Below and Point at the banana tree to Diagnose!",
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(top = 50.dp)
            )
        }
    }
}