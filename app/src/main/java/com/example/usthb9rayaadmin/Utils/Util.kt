package com.example.usthb9rayaadmin.Utils


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
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
            type = "vnd.android.cursor.dir/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
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

    fun deleteFileFromInternalStorage(context: Context, fileName: String): Boolean {

        val file = File(context.getExternalFilesDir(null), fileName)
        Log.e("FileExists", file.exists().toString())
        return file.delete()


    }

}