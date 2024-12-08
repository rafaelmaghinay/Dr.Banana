package com.example.drbanana

import android.app.AlertDialog
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@Composable
fun FeedbackScreen(navController: NavHostController) {
    var rating by remember { mutableStateOf(0) }
    val viewModel = DiseaseViewModel()
    var feedbackText by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                            sendFeedbackEmail(context, feedbackText, rating)
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

fun sendFeedbackEmail(context: Context, feedbackText: String, rating: Int) {
    val client = OkHttpClient()
    val json = JSONObject().apply {
        put("personalizations", JSONArray().apply {
            put(JSONObject().apply {
                put("to", JSONArray().apply {
                    put(JSONObject().apply {
                        put("email", "rafaelfmaghinay@su.edu.ph")
                    })
                })
                put("subject", "Feedback from App")
            })
        })
        put("from", JSONObject().apply {
            put("email", "drbanana036@gmail.com")
        })
        put("content", JSONArray().apply {
            put(JSONObject().apply {
                put("type", "text/plain")
                put("value", "Rating: $rating\nFeedback: $feedbackText")
            })
        })
    }

    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
    val apiKey = BuildConfig.SENDGRID_API_KEY
    val request = Request.Builder()
        .url("https://api.sendgrid.com/v3/mail/send")
        .post(body)
        .addHeader("Authorization", "Bearer $apiKey")
        .addHeader("Content-Type", "application/json")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            (context as MainActivity).runOnUiThread {
                showAlertDialog(context, "Error", "Failed to send feedback email.")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            (context as MainActivity).runOnUiThread {
                if (response.isSuccessful) {
                    showAlertDialog(context, "Success", "Feedback email sent successfully.")
                } else {
                    showAlertDialog(context, "Error", "Failed to send feedback email. Code: ${response.code}")
                }
            }
        }
    })
}

fun showAlertDialog(context: Context, title: String, message: String) {
    val positiveButtonText = if (title == "Success") "OK" else "Try again Later"
    val positiveButtonColor = if (title == "Success") Color.Green else Color.Red

    val alertDialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText, null)
        .create()

    alertDialog.setOnShowListener {
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(positiveButtonColor)
        positiveButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    alertDialog.show()
}