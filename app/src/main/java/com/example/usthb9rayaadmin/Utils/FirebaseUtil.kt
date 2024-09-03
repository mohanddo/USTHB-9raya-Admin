package com.example.usthb9rayaadmin.Utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object FirebaseUtil {
    private val storage = Firebase.storage
    var storageRef = storage.reference

    private val database = Firebase.database
    val contributionsRef = database.getReference("contributions")

    fun downloadFileToInternalStorage(context: Context, contributionId: String, fileExtension: String, progressBar: ContentLoadingProgressBar, downloadButt: AppCompatButton, openFileButton: AppCompatButton, dataStore: DataStore<Preferences>) {

        val storageReference = storageRef.child("uploads/${contributionId}")

//        val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val file = File(externalStorageDir, "${fileName}.${fileExtension}")


        progressBar.show()
        progressBar.progress = 0
        downloadButt.isEnabled = false

        val file = File(context.getExternalFilesDir(null), "${contributionId}.${fileExtension}")
        storageReference.getFile(file).addOnSuccessListener {

//            updateTheIsFileDownloadedField(fileName)
            CoroutineScope(Dispatchers.IO).launch {
                Util.save(contributionId, "true", dataStore)
            }
            Toast.makeText(context, "File downloaded to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            Log.e("FileDownloader", "File downloaded to ${file.absolutePath}")
            progressBar.hide()
            downloadButt.visibility = View.GONE
            openFileButton.visibility = View.VISIBLE

        }.addOnFailureListener { exception ->
            progressBar.hide()
            Toast.makeText(context, "Failed to download file, please try again.", Toast.LENGTH_SHORT).show()
            Log.e("FileDownloader", "Failed to download file: ${exception.message}")

        }.addOnProgressListener { taskSnapshot ->
            val bytesTransferred = taskSnapshot.bytesTransferred
            val totalByteCount = taskSnapshot.totalByteCount
            val progressPercentage = (100.0 * bytesTransferred / totalByteCount).toInt()
            progressBar.progress = progressPercentage
        }
    }

//    private fun updateTheIsFileDownloadedField(contributionId: String) {
//        contributionsRef.child(contributionId).child("fileDownloaded").setValue("true")
//    }

}