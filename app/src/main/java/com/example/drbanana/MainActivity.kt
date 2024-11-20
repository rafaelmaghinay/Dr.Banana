package com.example.drbanana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.drbanana.ui.theme.DrBananaTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.drbanana.ui.Navigation
import com.example.drbanana.ui.SplashScreen
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrBananaTheme {
                AppStart()
            }
        }
    }
}

@Composable
fun AppStart(){
    val isSplashScreenVisible = remember { mutableStateOf(true) }

    // Control splash screen display duration
    LaunchedEffect(Unit) {
        delay(4000)  // Show splash screen for 2 seconds
        isSplashScreenVisible.value = false
    }

    if (isSplashScreenVisible.value) {
        SplashScreen()
    } else {
        Navigation()  // Navigate to your main screen after splash
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DrBananaTheme {
        SplashScreen()
    }
}