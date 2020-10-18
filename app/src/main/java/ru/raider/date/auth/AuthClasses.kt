package ru.raider.date.auth

import com.google.gson.annotations.SerializedName

data class SignupRequest (
        @SerializedName("name") val name: String,
        @SerializedName("surname") val surname: String,
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String,
        @SerializedName("age") val age: Int
)

data class SignupResponse (
        @SerializedName("error")
        var error: String,

        @SerializedName("auth_token")
        var authToken: String
)

data class LoginRequest (
        @SerializedName("email")
        var email: String,

        @SerializedName("password")
        var password: String
)

data class LoginResponse (
        @SerializedName("error")
        var error: String,

        @SerializedName("auth_token")
        var authToken: String
)

data class SimpleResponse (
        @SerializedName("error")
        var error: String,

        @SerializedName("success")
        var success: String
)