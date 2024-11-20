package com.example.drbanana.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class ModelInference(private val context: Context) {

    private val interpreter: Interpreter

    init {
        interpreter = createInterpreter(context, "model.tflite")
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun createInterpreter(context: Context, modelName: String): Interpreter {
        val model = loadModelFile(context, modelName)
        return Interpreter(model)
    }

    fun runInference(input: FloatArray): FloatArray {
        val output = FloatArray(4) // Adjust the size based on your model's output
        try {
            Log.d("ModelInference", "Running inference...")
            interpreter.run(input, output)
            Log.d("ModelInference", "Inference completed successfully.")
        } catch (e: Exception) {
            Log.e("ModelInference", "Error during inference: ${e.message}", e)
        }
        return output
    }
}

fun preprocessInput(bitmap: Bitmap): FloatArray {
    val width = 224
    val height = 224
    val floatArray = FloatArray(width * height * 3)
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = resizedBitmap.getPixel(x, y)
            val index = (y * width + x) * 3
            floatArray[index] = (pixel shr 16 and 0xFF) / 255.0f // Red normalized to [0, 1]
            floatArray[index + 1] = (pixel shr 8 and 0xFF) / 255.0f // Green normalized
            floatArray[index + 2] = (pixel and 0xFF) / 255.0f // Blue normalized
        }
    }
    return floatArray
}

fun postprocessOutput(output: FloatArray): List<String> {
    val classNames = listOf("Cordana", "Healthy", "Panama", "Sigatoka")
    val maxIndex = output.indices.maxByOrNull { output[it] } ?: -1

    if (output.all { it == 0.0f }) {
        return listOf("Invalid prediction. Check input or model!")
    }

    return if (maxIndex != -1) {
        listOf(classNames[maxIndex])
    } else {
        listOf("No confident prediction found.")
    }
}

