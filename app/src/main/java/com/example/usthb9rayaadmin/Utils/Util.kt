package com.example.usthb9rayaadmin.Utils


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Util {

    fun calculateDateFromTimestamp(timestamp: Long): String {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        return formattedDate
    }

    fun mimeTypeToExtension(mimeType: String): String? {
        return when (mimeType) {
            "text/plain" -> "txt"
            "text/html" -> "html"
            "text/css" -> "css"
            "text/javascript" -> "js"
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/svg+xml" -> "svg"
            "image/webp" -> "webp"
            "audio/mpeg" -> "mp3"
            "audio/ogg" -> "ogg"
            "audio/wav" -> "wav"
            "video/mp4" -> "mp4"
            "video/webm" -> "webm"
            "video/ogg" -> "ogv"
            "application/pdf" -> "pdf"
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
            "application/vnd.ms-excel" -> "xls"
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "application/vnd.ms-powerpoint" -> "ppt"
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx"
            "application/zip" -> "zip"
            "application/x-rar-compressed" -> "rar"
            "application/json" -> "json"
            "application/xml" -> "xml"
            "application/x-www-form-urlencoded" -> "urlencoded"
            else -> null
        }
    }

    fun sendEmail(context: Context, email: String, subject: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            setPackage("com.google.android.gm")
        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch (e: Exception) {
            Toast.makeText(context, "Gmail app is not installed", Toast.LENGTH_SHORT).show()
        }

    }

    fun alertDialog(context: Context, title: String, message: String, positiveButtonMessage: String,
                    negativeButtonMessage: String? = null, positiveButtonAction: () -> Unit, negativeButtonAction: ((DialogInterface) -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButtonMessage) { _, _ ->
            positiveButtonAction.invoke()
        }
        negativeButtonMessage?.let {
            builder.setNegativeButton(it) { dialog, _ ->
                negativeButtonAction?.invoke(dialog)
            }
        }

        builder.show()

    }

    fun deleteFileFromInternalStorage(context: Context, contributionId: String, ext: String): Boolean {

        CoroutineScope(Dispatchers.IO).launch {
            delete(contributionId, DataStoreProvider.getInstance(context))
        }

        val file = File(context.getExternalFilesDir(null), "${contributionId}.${ext}")
        Log.e("FileExists", file.exists().toString())
        return file.delete()
    }

    suspend fun save(key: String, value: String, dataStore: DataStore<Preferences>) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun read(key: String, dataStore: DataStore<Preferences>): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    private suspend fun delete(key: String, dataStore: DataStore<Preferences>) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings.remove(dataStoreKey)
        }
    }


    fun openFileFromInternalStorage(context: Context, fileName: String, mimeType: String) {
        try {

            val file = File(context.getExternalFilesDir(null), fileName)

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(context, "Error opening file, please try again.", Toast.LENGTH_LONG).show()
            Log.e("FileDownloader", "Error opening file: ${e}")
        }
    }

}