package com.example.drbanana.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.drbanana.Disease
import com.example.drbanana.DiseaseViewModel
import com.example.drbanana.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.format

@Composable
fun RecommendationsScreen(diseaseId: String?, navController: NavHostController) {
    val diseaseViewModel: DiseaseViewModel = viewModel()
    var disease by remember { mutableStateOf<Disease?>(null) }
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    BackHandler {
        navController.navigate("home") {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }

    if (diseaseId != null) {
        val objectId = ObjectId(diseaseId)
        LaunchedEffect(objectId) {
            withContext(Dispatchers.IO) {
                val result = diseaseViewModel.getDiseaseById(objectId)
                withContext(Dispatchers.Main) {
                    disease = result
                }
            }
        }
    }

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

                        disease?.let {
                            val formattedDate = dateFormat.format(it.dateTaken)
                            Text(
                                text = formattedDate,
                                color = Color.Black,
                                fontWeight = Bold,
                                fontSize = 24.sp,
                            )
                        }
                    }
                }
            }

            item {
                disease?.let {
                    Text(
                        "Diagnosis Result",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .aspectRatio(1f)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = Uri.parse(it.imageUri),
                                    onError = { error ->
                                        Log.e(
                                            "ImageLoading",
                                            "Error loading image: ${error.result.throwable}"
                                        )
                                    },
                                    placeholder = painterResource(R.drawable.placeholder_image)
                                ),
                                contentDescription = "Disease Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column {
                            Text(
                                text = it.treeDisease,
                                fontSize = 16.sp,
                                fontWeight = Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(16.dp)
                            )

                            Text(
                                text = "fungus",
                                fontSize = 16.sp,
                                color = Color(0xFF585858),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray, thickness = 1.dp)

                    Text(
                        "Recommendations",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    when(it.treeDisease) {
                        "Cordana Leaf Spot" -> { CordanaRec() }
                        "Panama Disease" -> { PanamaRec() }
                        "Black/Yellow Sigatoka" -> { BlackSigRec() }
                    }

                    Text(
                        "How useful was this information?",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("feedback")
                            },
                            modifier = Modifier
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF61AF2B),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Report Feedback")
                        }
                    }

                } ?: Text("Loading...")
            }
        }
    }
}

@Composable
fun PanamaRec(){
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            "Beneficial Microorganisms",
            fontSize = 18.sp,
            fontWeight = Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Introduce beneficial fungi, bacteria, or organic amendments to improve soil health, which may help suppress pathogenic Fusarium populations.",
            fontSize = 16.sp,
            color = Color(0xFF585858),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Organic Matter",
            fontSize = 18.sp,
            fontWeight = Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Apply organic composts to boost soil health, which can support beneficial microbial communities.",
            fontSize = 16.sp,
            color = Color(0xFF585858)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun BlackSigRec() {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            "Chemical Control",
            fontSize = 18.sp,
            fontWeight = Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Apply fungicides as per local agricultural guidelines. Rotate fungicides with different modes of action to prevent resistance.",
            fontSize = 16.sp,
            color = Color(0xFF585858),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Cultural Practices",
            fontSize = 18.sp,
            fontWeight = Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Remove and destroy infected leaves to reduce the spread of the disease. Ensure proper spacing between plants to improve air circulation and reduce humidity. Avoid overhead irrigation to minimize leaf wetness.",
            fontSize = 16.sp,
            color = Color(0xFF585858)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun CordanaRec() {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            "Fungicide Application",
            fontSize = 18.sp,
            fontWeight = Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Apply fungicides containing chlorothalonil, copper-based compounds, or maneb as per the manufacturer’s recommendations. Rotate fungicides with different modes of action to prevent the development of resistance.",
            fontSize = 16.sp,
            color = Color(0xFF585858),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Soil and Nutrient Management",
            fontSize = 18.sp,
            fontWeight = Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Ensure plants receive balanced fertilization to maintain vigor, as healthy plants are less susceptible to diseases. Apply organic matter to improve soil health, which may help in disease suppression.",
            fontSize = 16.sp,
            color = Color(0xFF585858)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}