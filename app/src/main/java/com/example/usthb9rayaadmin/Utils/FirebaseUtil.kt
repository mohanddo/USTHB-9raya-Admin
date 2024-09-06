package com.example.usthb9rayaadmin.Utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
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

    fun downloadFileToInternalStorage(context: Context, fileNames: List<String>, contributionId: String, filesSize: Long, progressBar: ContentLoadingProgressBar, downloadButt: AppCompatButton, openFileButton: AppCompatButton, dataStore: DataStore<Preferences>) {

        val storageReference = storageRef.child("uploads/${contributionId}")

//        val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val file = File(externalStorageDir, "${fileName}.${fileExtension}")

        var succeededDownloads = 0
        progressBar.show()
        progressBar.progress = 0
        progressBar.max = fileNames.size * 100
        downloadButt.isEnabled = false

        var totalBytesTransferred: Long = 0

        for(fileName in fileNames) {

            val file = File(context.getExternalFilesDir(null), fileName)
            val downloadTask = storageReference.child(fileName).getFile(file)
            downloadTask.addOnSuccessListener {
                //  updateTheIsFileDownloadedField(fileName)
//                Toast.makeText(context, "File downloaded to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                Log.e("FileDownloader", "File downloaded to ${file.absolutePath}")

                succeededDownloads++

                if (succeededDownloads == fileNames.size) {
                    CoroutineScope(Dispatchers.IO).launch {
                        Util.save(contributionId, "true", dataStore)
                    }
                    progressBar.hide()
                    downloadButt.visibility = View.GONE
                    openFileButton.visibility = View.VISIBLE
                }

            }.addOnFailureListener { exception ->
                progressBar.hide()
                downloadButt.isEnabled = true
                Toast.makeText(context, "Failed to download file, please try again.", Toast.LENGTH_SHORT).show()
                Log.e("FileDownloader", "Failed to download file: ${exception.message}")

            }.addOnProgressListener { taskSnapshot ->
                totalBytesTransferred += taskSnapshot.bytesTransferred
                updateProgressBar(progressBar, totalBytesTransferred, filesSize)
            }
        }
        }

    private fun updateProgressBar(progressBar: ContentLoadingProgressBar, totalBytesTransferred: Long, totalBytes: Long) {
        val progress = (100.0 * totalBytesTransferred / totalBytes).toInt()
        progressBar.progress = progress
    }

//    private fun updateTheIsFileDownloadedField(contributionId: String) {
//        contributionsRef.child(contributionId).child("fileDownloaded").setValue("true")
//    }

    fun deleteContributionFromFirebase(
        contributionId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val directoryRef = storageRef.child("uploads/$contributionId")

        // List all files in the directory
        directoryRef.listAll().addOnSuccessListener { listResult ->
            val deleteTasks = listResult.items.map { fileRef ->
                fileRef.delete()
            }

            // Wait for all delete tasks to finish
            Tasks.whenAll(deleteTasks).addOnSuccessListener {
                // After successfully deleting all files, remove the contribution from the database
                contributionsRef.child(contributionId).removeValue().addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener { databaseError ->
                    onFailure()
                    Log.e("ContributionDeletion", "Database deletion failed: ${databaseError.message}")
                }
            }.addOnFailureListener { storageError ->
                onFailure()
                Log.e("ContributionDeletion", "Storage deletion failed: ${storageError.message}")
            }
        }.addOnFailureListener { listError ->
            // Handle error in listing files
            if (listError is StorageException && listError.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                // If directory is empty or doesn't exist, just delete the contribution entry
                contributionsRef.child(contributionId).removeValue().addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener { databaseError ->
                    onFailure()
                    Log.e("ContributionDeletion", "Database deletion failed: ${databaseError.message}")
                }
            } else {
                onFailure()
                Log.e("ContributionDeletion", "Failed to list files: ${listError.message}")
            }
        }
    }

}