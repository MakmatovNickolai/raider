package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class CheckAuthReponse (
        @SerializedName("error") val error: String,
        @SerializedName("result") val result: User
)