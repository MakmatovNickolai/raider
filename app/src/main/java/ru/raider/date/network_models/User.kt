package ru.raider.date.network_models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
        @SerializedName("id") val id: String? = "",
        @SerializedName("name") val name: String,
        @SerializedName("city") val city: String,
        @SerializedName("age") val age: Int,
        @SerializedName("picture_url") val pictureUrl: String,
        @SerializedName("sex") val sex: String,
        @SerializedName("email")
        val email: String? = "",
        @SerializedName("password")
        val password: String? = ""
): Parcelable