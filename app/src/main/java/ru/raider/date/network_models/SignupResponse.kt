package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class SignupResponse (
        @SerializedName("error")
        var error: String,

        @SerializedName("auth_token")
        var authToken: String,

        @SerializedName("user_random_hash")
        var userRandomHash: String
)