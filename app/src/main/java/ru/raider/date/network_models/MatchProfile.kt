package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class MatchProfile (
        @SerializedName("name")
        val name: String,
        @SerializedName("picture_url")
        val pictureUrl: String
)