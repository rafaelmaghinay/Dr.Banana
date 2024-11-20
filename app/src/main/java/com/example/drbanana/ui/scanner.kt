package com.example.drbanana.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.navigation.NavHostController
import com.example.drbanana.R

@Composable
fun ScanButton(onImageCaptured: (Uri?) -> Unit, navController: NavHostController) {
    val context = LocalContext.current
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val predictionResultState = remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        handleImageCapture(success, imageUriState.value, context, navController, predictionResultState, onImageCaptured)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        handleImageSelection(uri, context, navController, predictionResultState, imageUriState, onImageCaptured)
    }

    if (showDialog.value) {
        ChooseImageSourceDialog(showDialog) { option ->
            if (option == "Take Picture") {
                val imageUri = createImageUri(context)
                imageUri?.let {
                    imageUriState.value = it
                    cameraLauncher.launch(it)
                }
            } else {
                galleryLauncher.launch("image/*")
            }
            showDialog.value = false
        }
    }

    Button(
        onClick = { showDialog.value = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF61AF2B),
            contentColor = Color.White
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(150.dp)
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

private fun handleImageCapture(
    success: Boolean,
    uri: Uri?,
    context: Context,
    navController: NavHostController,
    predictionResultState: MutableState<String>,
    onImageCaptured: (Uri?) -> Unit
) {
    if (success) {
        val bitmap = uri?.let { getBitmapFromUri(context, it) }
        if (bitmap != null) {
            Log.d("ScanButton", "Bitmap captured: $bitmap")
            val predictionResult = processImageWithModel(context, bitmap)
            predictionResultState.value = predictionResult.joinToString(", ")
            Log.d("ScanButton", "Prediction result: $predictionResult")
            navController.navigate("result/${predictionResultState.value}")
        }
        onImageCaptured(uri)
    } else {
        Log.d("ScanButton", "Image capture failed")
        onImageCaptured(null)
    }
}

private fun handleImageSelection(
    uri: Uri?,
    context: Context,
    navController: NavHostController,
    predictionResultState: MutableState<String>,
    imageUriState: MutableState<Uri?>,
    onImageCaptured: (Uri?) -> Unit
) {
    if (uri != null) {
        Log.d("ScanButton", "Image selected: $uri")
        val bitmap = getBitmapFromUri(context, uri)
        if (bitmap != null) {
            Log.d("ScanButton", "Bitmap selected: $bitmap")
            val predictionResult = processImageWithModel(context, bitmap)
            predictionResultState.value = predictionResult.joinToString(", ")
            Log.d("ScanButton", "Prediction result: $predictionResult")
            navController.navigate("result/${predictionResultState.value}")
        } else {
            Log.d("ScanButton", "Failed to decode bitmap from URI: $uri")
        }
        imageUriState.value = uri
        onImageCaptured(uri)
    } else {
        Log.d("ScanButton", "No image selected")
        onImageCaptured(null)
    }
}

private fun createImageUri(context: Context): Uri? {
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "new_image.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ComposeCamera")
        }
    )
}

fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        Log.e("getBitmapFromUri", "Error decoding bitmap from URI: $uri", e)
        null
    }
}

fun processImageWithModel(context: Context, bitmap: Bitmap): FloatArray {
    val modelInference = ModelInference(context)
    val input = convertBitmapToFloatArray(bitmap)
    return modelInference.runInference(input)
}

fun convertBitmapToFloatArray(bitmap: Bitmap): FloatArray {
    val width = 224
    val height = 224
    val floatArray = FloatArray(width * height * 3)
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = resizedBitmap.getPixel(x, y)
            val index = (y * width + x) * 3
            floatArray[index] = (pixel shr 16 and 0xFF) / 255.0f // Red
            floatArray[index + 1] = (pixel shr 8 and 0xFF) / 255.0f // Green
            floatArray[index + 2] = (pixel and 0xFF) / 255.0f // Blue
        }
    }
    Log.d("convertBitmapToFloatArray", "Float array: ${floatArray.joinToString(", ")}")
    return floatArray
}

@Composable
fun ChooseImageSourceDialog(showDialog: MutableState<Boolean>, onChoose: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Choose an option") },
        text = { Text("Do you want to take a picture or select from the gallery?") },
        confirmButton = {
            Button(onClick = { onChoose("Take Picture") }) {
                Text("Take Picture")
            }
        },
        dismissButton = {
            Button(onClick = { onChoose("Select from Gallery") }) {
                Text("Select from Gallery")
            }
        }
    )
}