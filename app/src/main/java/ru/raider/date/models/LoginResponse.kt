package ru.raider.date.models

import com.google.gson.annotations.SerializedName

data class LoginResponse (
        @SerializedName("error")
        var error: String,

        @SerializedName("auth_token")
        var authToken: String,

        @SerializedName("user_random_hash")
        var userRandomHash: String
)