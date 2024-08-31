package com.example.usthb9rayaadmin.Utils


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

}