package com.example.usthb9rayaadmin.dataClass

data class Contribution(val fullName: String = "",
                        val email: String = "",
                        val faculty: String = "",
                        val module: String = "",
                        val type: String = "",
                        val comment: String? = null,
                        val fileUrl: String = "",
                        val contributionId: String = "",
                        val timestamp: Long = System.currentTimeMillis()
)
