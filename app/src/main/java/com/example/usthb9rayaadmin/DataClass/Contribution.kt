package com.example.usthb9rayaadmin.DataClass

import android.os.Parcel
import android.os.Parcelable

data class Contribution(val fullName: String = "",
                        val email: String = "",
                        val faculty: String = "",
                        val module: String = "",
                        val type: String = "",
                        val comment: String? = null,
                        val fileUrls: List<String>? = null,
                        val fileNames: List<String>? = null,
                        val contributionId: String = "",
                        val filesSize: Long = 0,
                        val timestamp: Long = System.currentTimeMillis(),
    val youtubeLink: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        fullName = parcel.readString() ?: "",
        email = parcel.readString() ?: "",
        faculty = parcel.readString() ?: "",
        module = parcel.readString() ?: "",
        type = parcel.readString() ?: "",
        comment = parcel.readString(),
        fileUrls = parcel.createStringArrayList(),
        fileNames = parcel.createStringArrayList(),
        contributionId = parcel.readString() ?: "",
        filesSize = parcel.readLong(),
        timestamp = parcel.readLong(),
        youtubeLink = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(faculty)
        parcel.writeString(module)
        parcel.writeString(type)
        parcel.writeString(comment)
        parcel.writeStringList(fileUrls)
        parcel.writeStringList(fileNames)
        parcel.writeString(contributionId)
        parcel.writeLong(filesSize)
        parcel.writeLong(timestamp)
        parcel.writeString(youtubeLink)
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
