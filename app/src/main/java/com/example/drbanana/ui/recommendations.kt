package com.example.drbanana.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.drbanana.Disease
import com.example.drbanana.DiseaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId

@Composable
fun RecommendationsScreen(diseaseId: String?, navController: NavHostController) {
    val diseaseViewModel: DiseaseViewModel = viewModel()
    var disease by remember { mutableStateOf<Disease?>(null) }

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
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (disease != null) {
            Text("Recommendations for disease: ${disease!!.treeDisease}, Image URI: ${disease!!.imageUri}")
        } else {
            Text("Loading...")
        }
    }
}