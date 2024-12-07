package com.example.drbanana.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

    if (disease != null) {
        Text("Recommendations for disease: ${disease!!.treeDisease}, Image URI: ${disease!!.imageUri}")
    } else {
        Text("Loading...")
    }
}