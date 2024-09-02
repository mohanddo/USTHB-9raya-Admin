package com.example.usthb9rayaadmin.Utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

object FirebaseUtil {
    private val storage = Firebase.storage
    var storageRef = storage.reference

    private val database = Firebase.database
    val contributionsRef = database.getReference("contributions")

    fun downloadFileToInternalStorage(context: Context, fileName: String, fileExtension: String, progressBar: ProgressBar, downloadButt: AppCompatButton, openButt: AppCompatButton) {

        val storageReference = storageRef.child("uploads/${fileName}")

//        val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val file = File(externalStorageDir, "${fileName}.${fileExtension}")

        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        downloadButt.visibility = View.GONE

        val file = File(context.getExternalFilesDir(null), "${fileName}.${fileExtension}")
        storageReference.getFile(file).addOnSuccessListener {

            updateTheIsFileDownloadedField(fileName)
            Toast.makeText(context, "File downloaded to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            Log.e("FileDownloader", "File downloaded to ${file.absolutePath}")
            progressBar.visibility = View.GONE
            openButt.visibility = View.VISIBLE

        }.addOnFailureListener { exception ->
            progressBar.visibility = View.GONE
            downloadButt.visibility = View.VISIBLE
            Toast.makeText(context, "Failed to download file, please try again.", Toast.LENGTH_SHORT).show()
            Log.e("FileDownloader", "Failed to download file: ${exception.message}")

        }.addOnProgressListener { taskSnapshot ->
            val bytesTransferred = taskSnapshot.bytesTransferred
            val totalByteCount = taskSnapshot.totalByteCount
            val progressPercentage = (100.0 * bytesTransferred / totalByteCount).toInt()
            progressBar.progress = progressPercentage
        }
    }

    private fun updateTheIsFileDownloadedField(contributionId: String) {
        contributionsRef.child(contributionId).child("fileDownloaded").setValue("true")
    }

}