package ru.raider.date.models

import com.google.gson.annotations.SerializedName

data class User (
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("surname") val surname: String,
        @SerializedName("age") val age: Int,
        @SerializedName("picture_url") val pictureUrl: String,
        @SerializedName("sex") val sex: String,
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String
)