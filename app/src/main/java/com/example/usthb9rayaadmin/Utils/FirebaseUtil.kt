package com.example.usthb9rayaadmin.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseUtil {
    private val storage = Firebase.storage
    var storageRef = storage.reference

    private val database = Firebase.database
    val contributionsRef = database.getReference("contributions")
}