package com.example.drbanana.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.drbanana.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.drbanana.Disease
import com.example.drbanana.DiseaseViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(navController : NavHostController, diseaseViewModel: DiseaseViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        diseaseViewModel.loadDiseases()
    }
    val diseases by diseaseViewModel.diseases.observeAsState(emptyList())
    val deleteAllDialog = remember { mutableStateOf(false) }

    if (deleteAllDialog.value) {
        AlertDialog(
            onDismissRequest = { deleteAllDialog.value = false },
            title = { Text(text = "Delete All Items?", color = Color.Black) },
            confirmButton = {
                TextButton(onClick = {
                    deleteAllDialog.value = false
                    diseaseViewModel.deleteAllDiseases()
                }) {
                    Text("Confirm", color = Color(0xFF61AF2B))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    deleteAllDialog.value = false
                }) {
                    Text("Cancel", color = Color(0xFF61AF2B))
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color(0xFFD0FFCF),
                        0.6f to Color(0xFFD0FFCF),
                        1f to Color(0xFFFFFFFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column {
            Row {
                Text(
                    text = "Your Diagnoses",
                    fontWeight = Bold,
                    color = Color.Black,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Delete All",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            deleteAllDialog.value = true
                        }
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(diseases) { disease ->
                    DiseaseItem(disease, navController)
                }
            }
        }
    }
}

@Composable
fun DiseaseItem(disease: Disease,  navController: NavHostController) {
    val diseaseViewModel : DiseaseViewModel = viewModel()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(disease.dateTaken)
    val deleteDialog = remember { mutableStateOf(false) }

    if (deleteDialog.value) {
        AlertDialog(
            onDismissRequest = { deleteDialog.value = false },
            title = { Text(text = "Are You Sure You Want To Delete Item?", color = Color.Black) },
            confirmButton = {
                TextButton(onClick = {
                    deleteDialog.value = false
                    diseaseViewModel.deleteDisease(disease.id)
                }) {
                    Text("Confirm", color = Color(0xFF61AF2B))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    deleteDialog.value = false
                }) {
                    Text("Cancel", color = Color(0xFF61AF2B))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("recommendations/${disease.id}")
            }
    ) {
        Row{
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = Uri.parse(disease.imageUri),
                        onError = { error ->
                            Log.e("ImageLoading", "Error loading image: ${error.result.throwable}")
                        },
                        placeholder = painterResource(R.drawable.placeholder_image)
                    ),
                    contentDescription = "Disease Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = formattedDate, fontSize = 12.sp, color = Color(0xFF585858))
                Spacer(modifier = Modifier.height(11.dp))
                Text(
                    text = "${disease.treeDisease}",
                    fontSize = 16.sp,
                    fontWeight = Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Delete Icon",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(30.dp)
                    .clickable(onClick = {
                        // Delete the disease from the database
                        deleteDialog.value = true
                    })
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)

    }
}