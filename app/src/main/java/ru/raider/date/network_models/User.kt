package ru.raider.date.network_models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
        @SerializedName("id") val id: String? = "",
        @SerializedName("name") var name: String,
        @SerializedName("city") var city: String,
        @SerializedName("age") var age: Int,
        @SerializedName("description") var description: String? = "",
        @SerializedName("main_picture_url") var main_picture_url: String? = "",
        @SerializedName("picture_urls") var pictureUrls: MutableList<String>? = mutableListOf<String>(),
        //var pictureUrls: MutableList<PictureUrl>? = mutableListOf<PictureUrl>(),
        @SerializedName("sex") var sex: String,
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("password")
        var password: String? = "",
        @SerializedName("long")
        var long: Double? = 0.0,
        @SerializedName("lat")
        var lat: Double? = 0.0
) : Parcelable