package com.example.drbanana.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HelpScreen(navController: NavHostController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFFD0FFCF))
                        .padding(12.dp),
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = com.example.drbanana.R.drawable.ex),
                            contentDescription = "exit",
                            modifier = Modifier
                                .size(35.dp)
                                .clickable(onClick = {
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                })
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Help",
                            color = Color.Black,
                            fontWeight = Bold,
                            fontSize = 24.sp,

                            )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Welcome to Dr. Banana!",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Q: What is this app for?",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A: This app helps farmers and agricultural enthusiasts to identify common banana plant diseases using images of banana leaves. It works offline, so no internet connection is required.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Q: How does it work?",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "A: \n - Open the app \n - Take a clear picture of the banana leaf using your phone's camera or upload an existing image.\n - The app analyzes the image and provides a disease diagnosis along with treatment recommendations.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Q: What are the diseases that can be diagnosed in this app?",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "A: The app can diagnose the following:",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Cordana Leaf Spot",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Black/Yellow Sigatoka",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Panama Disease",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Healthy Banana Leaf",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Q: How do I take a good photo?",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A: To take a good photo of a banana leaf and ensure an accurate classification, follow these steps:",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Ensure the leaf is well-lit (prefer natural daylight).",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Avoid shadows or blurry images.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Capture the entire leaf in the frame.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- Capture the affected part of the leaf clearly.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Q: What if the app gives no result?",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A: If the app gives no result, it may be due to the following reasons:",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- The image is not clear or well-lit. Try taking another photo with better lighting and clarity.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                        Text(
                            text = "- The image does not contain a banana leaf. Ensure the image contains a banana leaf and not other plants or objects.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Q: Can I use the app without the internet?",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A: Yes! This app was designed to work completely offline.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Q: How accurate is the app?",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A: The app uses a trained model specifically for banana disease classification. While it has a relatively high accuracy, results may vary depending on image quality and the stage of the disease.",
                            fontSize = 16.sp,
                            color = Color(0xFF585858)
                        )
                    }
                }

            }
        }
    }
}