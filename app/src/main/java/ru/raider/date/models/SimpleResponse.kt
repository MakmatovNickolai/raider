package ru.raider.date.models

import com.google.gson.annotations.SerializedName

data class SimpleResponse (
        @SerializedName("error")
        var error: String,

        @SerializedName("result")
        var result: String
)