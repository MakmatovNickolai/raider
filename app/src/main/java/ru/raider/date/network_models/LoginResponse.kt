package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class LoginResponse (
        @SerializedName("error")
        val error: String,

        @SerializedName("auth_token")
        val authToken: String,

        @SerializedName("user_random_hash")
        val userRandomHash: String,

        @SerializedName("user")
        val user: User
)