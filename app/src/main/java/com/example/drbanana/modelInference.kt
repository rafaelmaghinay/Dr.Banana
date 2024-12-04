package com.example.drbanana

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.example.drbanana.ml.Model



fun augmentImage(bitmap: Bitmap): Bitmap {
    val matrix = Matrix()

    // Apply random rotation
    val rotation = (Math.random() * 360).toFloat()
    matrix.postRotate(rotation)

    // Apply random flipping
    if (Math.random() > 0.5) {
        matrix.postScale(-1f, 1f)
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun bitmapToByteBuffer(bitmap: Bitmap, width: Int, height: Int): ByteBuffer {
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
    val byteBuffer = ByteBuffer.allocateDirect(4 * width * height * 3)
    byteBuffer.order(ByteOrder.nativeOrder())
    val intValues = IntArray(width * height)
    resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
    var pixel = 0
    for (i in 0 until height) {
        for (j in 0 until width) {
            val value = intValues[pixel++]
            byteBuffer.putFloat((value shr 16 and 0xFF) / 255.0f)  // Normalize R
            byteBuffer.putFloat((value shr 8 and 0xFF) / 255.0f)   // Normalize G
            byteBuffer.putFloat((value and 0xFF) / 255.0f)        // Normalize B
        }
    }
    return byteBuffer
}

fun classifyImage(context: Context, bitmap: Bitmap): FloatArray {
    try {
        Log.d("classifyImage", "Starting image classification")
        val model = Model.newInstance(context)
        Log.d("classifyImage", "Model loaded successfully")

        // Apply preprocessing
        val byteBuffer = bitmapToByteBuffer(bitmap, 224, 224)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)
        Log.d("classifyImage", "Input buffer loaded successfully")

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        Log.d("classifyImage", "Model inference completed successfully")

        // Log the output values for debugging
        outputFeature0.floatArray.forEachIndexed { index, value ->
            Log.d("classifyImage", "Output at index $index: $value")
        }

        // Releases model resources if no longer used.
        model.close()
        Log.d("classifyImage", "Model closed successfully")
        return outputFeature0.floatArray
    } catch (e: Exception) {
        Log.e("classifyImage", "Error during image classification", e)
        return floatArrayOf() // Return an empty float array in case of error
    }
}