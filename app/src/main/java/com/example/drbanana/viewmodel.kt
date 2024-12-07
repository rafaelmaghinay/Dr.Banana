package com.example.drbanana

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date

class DiseaseViewModel : ViewModel() {
    private val _diseases = MutableLiveData<List<Disease>>()
    val diseases: LiveData<List<Disease>> get() = _diseases

    init {
        loadDiseases()
    }

    fun loadDiseases() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = Realm.getDefaultInstance()
            val results = realm.where<Disease>().findAll()
            val diseasesList = realm.copyFromRealm(results)
            realm.close()
            withContext(Dispatchers.Main) {
                _diseases.value = diseasesList ?: emptyList()
            }
        }
    }

    fun addDisease(context: Context, diseaseName: String, imageUri: String, onDiseaseAdded: (ObjectId?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = Realm.getDefaultInstance()
            var diseaseId: ObjectId? = null
            val localImagePath = saveImageLocally(context, imageUri)
            realm.executeTransaction {
                val disease = Disease(
                    id = ObjectId(),
                    treeDisease = diseaseName,
                    dateTaken = Date(),
                    imageUri = localImagePath
                )
                it.insert(disease)
                diseaseId = disease.id
            }
            realm.close()
            loadDiseases()
            withContext(Dispatchers.Main) {
                onDiseaseAdded(diseaseId)
            }
        }
    }

    private fun saveImageLocally(context: Context, imageUri: String): String {
        val uri = Uri.parse(imageUri)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "images")
        if (!file.exists()) {
            file.mkdirs()
        }
        val imageFile = File(file, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(imageFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return imageFile.absolutePath
    }


    fun deleteDisease(diseaseId: ObjectId) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val disease = it.where<Disease>().equalTo("id", diseaseId).findFirst()
                disease?.deleteFromRealm()
            }
            realm.close()
            loadDiseases() // Reload the diseases to update the LiveData
        }
    }
}