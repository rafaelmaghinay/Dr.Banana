package com.example.drbanana.ui

import android.util.Log
import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ModelInference(context: Context) {

    private val model: Interpreter

    init {
        val modelFile = context.assets.open("model.tflite").use { inputStream ->
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            ByteBuffer.allocateDirect(buffer.size).apply {
                order(ByteOrder.nativeOrder())
                put(buffer)
            }
        }
        model = Interpreter(modelFile)
    }

    fun runInference(input: FloatArray): FloatArray {
        val output = FloatArray(4) // Change size based on your model's output
        model.run(input, output)
        Log.d("ModelInference", "Inference output: ${output.joinToString(", ")}")
        return output
    }
}