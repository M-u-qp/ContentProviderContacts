package com.example.testcontentprovider

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?
) : Parcelable
