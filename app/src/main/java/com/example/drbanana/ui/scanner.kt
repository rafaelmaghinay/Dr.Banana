package com.example.drbanana.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.drbanana.R
import com.example.drbanana.classifyImage
import java.io.File

import kotlin.compareTo
import kotlin.toString

@Composable
fun ScanButton(onImageCaptured: (Uri?) -> Unit, navController: NavHostController) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val classificationResults = remember { mutableStateOf<FloatArray?>(null) }

    if (showDialog.value) {
        ImagePicker(onImageCaptured = { uri ->
            uri?.let {
                bitmap.value = createBitmapFromUri(context, it)
                bitmap.value?.let { bmp ->
                    if (classificationResults.value == null) {
                        classificationResults.value = classifyImage(context, bmp)
                        Log.d("ScanButton", "Classification results: ${classificationResults.value}")
                        navController.navigate(
                            "result/${Uri.encode(it.toString())}/${classificationResults.value?.joinToString(",") ?: ""}"
                        )
                    }
                }
            }
            showDialog.value = false
        }, onDismiss = { showDialog.value = false })
    }

    Button(
        onClick = {
            showDialog.value = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF61AF2B),
            contentColor = Color.White
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "Scan", fontSize = 16.sp)
            Image(
                painter = painterResource(id = R.drawable.scanlogo),
                contentDescription = "scanner icon",
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}


@Composable
fun ImagePicker(onImageCaptured: (Uri?) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("ImagePicker", "Selected URI: $uri")
        onImageCaptured(uri)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            Log.d("ImagePicker", "Captured URI: ${imageUri.value}")
            onImageCaptured(imageUri.value)
        } else {
            onDismiss()
        }
    }

    chooseImageSource(context, onOptionSelected = { option ->
        when (option) {
            "Gallery" -> galleryLauncher.launch("image/*")
            "Camera" -> {
                val uri = createImageUri(context)
                imageUri.value = uri
                cameraLauncher.launch(uri)
            }
        }
    }, onDismiss = onDismiss)
}

@Composable
fun chooseImageSource(context: Context, onOptionSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            },
            title = { Text(text = "Choose Image Source", color = Color.Black) },
            text = { Text("Select an option to pick an image from gallery or capture using camera.", color = Color.Black) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onOptionSelected("Gallery")
                }) {
                    Text("Gallery", color = Color(0xFF61AF2B))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onOptionSelected("Camera")
                }) {
                    Text("Camera", color = Color(0xFF61AF2B))
                }
            },
            containerColor = Color.White
        )
    }
}

fun createImageUri(context: Context): Uri {
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File(imagesDir, "image.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", image)
}


fun createBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    Log.d("createBitmapFromUri", "Bitmap: $originalBitmap")
    return originalBitmap
}