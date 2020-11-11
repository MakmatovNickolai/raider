package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class PictureUrl (
    @SerializedName("url")
    val url: String,
    @SerializedName("is_main")
    var is_main: Boolean = false
)