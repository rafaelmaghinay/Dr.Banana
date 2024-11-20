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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


suspend fun getBitmapFromUri(context: Context, uri: Uri, targetWidth: Int, targetHeight: Int): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                originalBitmap?.let {
                    resizeBitmap(it, targetWidth, targetHeight)
                }
            }
        } catch (e: Exception) {
            Log.e("getBitmapFromUri", "Error decoding bitmap from URI: $uri", e)
            null
        }
    }
}

fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
}

fun handleImageCapture(
    success: Boolean,
    imageUri: Uri?,
    context: Context,
    navController: NavHostController,
    predictionResultState: MutableState<String>,
    onImageCaptured: (Uri?) -> Unit
) {
    Log.d("handleImageCapture", "Capture success: $success, imageUri: $imageUri")
    if (success) {
        imageUri?.let { uri ->
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = getBitmapFromUri(context, uri, 224, 224)
                if (bitmap != null) {
                    Log.d("Scanner", "Bitmap dimensions: ${bitmap.width} x ${bitmap.height}")
                    val predictionResult = processImageWithModel(context, bitmap)
                    predictionResultState.value = predictionResult.joinToString(", ")
                    navController.navigate("result/${predictionResultState.value}/${uri}")
                    onImageCaptured(uri)
                } else {
                    Log.e("Scanner", "Failed to retrieve bitmap from URI: $imageUri")
                }
            }
        }
    }
}

suspend fun handleImageSelection(
    uri: Uri?,
    context: Context,
    navController: NavHostController,
    predictionResultState: MutableState<String>,
    imageUriState: MutableState<Uri?>,
    onImageCaptured: (Uri?) -> Unit
) {
    uri?.let {
        imageUriState.value = it
        val bitmap = getBitmapFromUri(context, it, 224, 224)
        if (bitmap != null) {
            val predictionResult = processImageWithModel(context, bitmap)
            predictionResultState.value = predictionResult.joinToString(", ")
            navController.navigate("result/${predictionResultState.value}/${it}")
            onImageCaptured(it)
        }
    }
}

@Composable
fun ScanButton(onImageCaptured: (Uri?) -> Unit, navController: NavHostController) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    val predictionResultState = remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        handleImageCapture(success, imageUriState.value, context, navController, predictionResultState, onImageCaptured)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        CoroutineScope(Dispatchers.Main).launch {
            handleImageSelection(uri, context, navController, predictionResultState, imageUriState, onImageCaptured)
        }
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
        onClick = {
            showDialog.value = true
        },
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

fun processImageWithModel(context: Context, bitmap: Bitmap): List<String> {
    val modelInference = ModelInference(context)
    val input = preprocessInput(bitmap)
    val output = modelInference.runInference(input)
    return postprocessOutput(output)
}