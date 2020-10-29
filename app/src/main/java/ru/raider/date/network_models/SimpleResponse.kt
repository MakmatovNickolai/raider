package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class SimpleResponse (
        @SerializedName("error")
        var error: String,

        @SerializedName("result")
        var result: String
)