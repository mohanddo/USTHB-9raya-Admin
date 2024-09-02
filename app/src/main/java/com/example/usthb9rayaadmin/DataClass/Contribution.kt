package com.example.usthb9rayaadmin.DataClass

import android.os.Parcel
import android.os.Parcelable

data class Contribution(val fullName: String = "",
                        val email: String = "",
                        val faculty: String = "",
                        val module: String = "",
                        val type: String = "",
                        val comment: String? = null,
                        val fileUrl: String = "",
                        val contributionId: String = "",
                        val timestamp: Long = System.currentTimeMillis(),
    val mimeType: String = "",
    val isFileDownloaded: String = "false"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        fullName = parcel.readString() ?: "",
        email = parcel.readString() ?: "",
        faculty = parcel.readString() ?: "",
        module = parcel.readString() ?: "",
        type = parcel.readString() ?: "",
        comment = parcel.readString(),
        fileUrl = parcel.readString() ?: "",
        contributionId = parcel.readString() ?: "",
        timestamp = parcel.readLong(),
        mimeType = parcel.readString() ?: "",
        isFileDownloaded = parcel.readString() ?: "false"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(faculty)
        parcel.writeString(module)
        parcel.writeString(type)
        parcel.writeString(comment)
        parcel.writeString(fileUrl)
        parcel.writeString(contributionId)
        parcel.writeLong(timestamp)
        parcel.writeString(mimeType)
        parcel.writeString(isFileDownloaded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contribution> {
        override fun createFromParcel(parcel: Parcel): Contribution {
            return Contribution(parcel)
        }

        override fun newArray(size: Int): Array<Contribution?> {
            return arrayOfNulls(size)
        }
    }
}
