package com.example.drbanana

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
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drbanana.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@Composable
fun FeedbackScreen(navController: NavHostController) {
    var rating by remember { mutableStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFFD0FFCF))
                    .padding(12.dp),
            ) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.ex),
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
                        text = "Feedback",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = Color.Black
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
                        text = "Rate your experience",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BananaRating(rating = rating, onRatingChanged = { newRating ->
                        rating = newRating
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tell us what can be improved?",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        placeholder = { Text("Enter your feedback here") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray, // Change this to your desired color
                            unfocusedIndicatorColor = Color.Gray, // Change this to your desired color
                            cursorColor = Color(0xFF61AF2B) // Change this to your desired color
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF61AF2B),
                            contentColor = White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "Submit Feedback", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BananaRating(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            val imageRes = if (i <= rating) R.drawable.yellowbanana else R.drawable.banana
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Rating Star",
                modifier = Modifier
                    .size(60.dp)
                    .clickable { onRatingChanged(i) }
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}