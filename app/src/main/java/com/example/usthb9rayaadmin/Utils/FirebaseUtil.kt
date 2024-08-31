package com.example.usthb9rayaadmin.Utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

object FirebaseUtil {
    private val storage = Firebase.storage
    private var storageRef = storage.reference

    private val database = Firebase.database
    val contributionsRef = database.getReference("contributions")

    fun downloadFileToInternalStorage(context: Context, fileName: String, fileExtension: String) {

        val storageReference = storageRef.child("uploads/${fileName}")

//        val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val file = File(externalStorageDir, "${fileName}.${fileExtension}")

        val file = File(context.getExternalFilesDir(null), "${fileName}.${fileExtension}")
        storageReference.getFile(file).addOnSuccessListener {

            Toast.makeText(context, "File downloaded to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            Log.e("FileDownloader", "File downloaded to ${file.absolutePath}")
        }.addOnFailureListener { exception ->

            Toast.makeText(context, "Failed to download file: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e("FileDownloader", "Failed to download file: ${exception.message}")
        }
    }

}