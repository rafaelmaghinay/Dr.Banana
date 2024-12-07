package com.example.drbanana.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun RecommendationsScreen( diseaseId: String?, navController: NavHostController) {

    BackHandler {
        navController.navigate("home") {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }

    Text("Recommendations for disease with id $diseaseId")
}