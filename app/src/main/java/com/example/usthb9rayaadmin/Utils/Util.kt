package com.example.usthb9rayaadmin.Utils


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    fun extensionToMimeType(extension: String): String? {
        return when (extension.lowercase()) {
            "txt" -> "text/plain"
            "html" -> "text/html"
            "css" -> "text/css"
            "js" -> "text/javascript"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "svg" -> "image/svg+xml"
            "webp" -> "image/webp"
            "mp3" -> "audio/mpeg"
            "ogg" -> "audio/ogg"
            "wav" -> "audio/wav"
            "mp4" -> "video/mp4"
            "webm" -> "video/webm"
            "ogv" -> "video/ogg"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            "json" -> "application/json"
            "xml" -> "application/xml"
            "urlencoded" -> "application/x-www-form-urlencoded"
            else -> null
        }
    }

    fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        return fileName.substring(dotIndex + 1)
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

    fun deleteFilesFromInternalStorage(context: Context, contributionId: String, fileNames: List<String>) {

        for (fileName in fileNames) {
            val file = File(context.getExternalFilesDir(null), fileName)
            file.delete()
        }

        CoroutineScope(Dispatchers.IO).launch {
            delete(contributionId, DataStoreProvider.getInstance(context))
        }

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
            Log.e("FileDownloader", "Error opening file: $e")
        }
    }

    fun singleChoiceDialog(context: Context, options: Array<String>, title: String, textView: TextView) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)

        builder.setSingleChoiceItems(options, -1) { dialogInterface, which ->
            textView.text = options[which]
            dialogInterface.dismiss()
        }

        builder.show()
    }

    fun openYouTubeLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.setPackage("com.google.android.youtube")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }

}