package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class Room (
        @SerializedName("id")
        val id: String,
        @SerializedName("uniqueUsersId")
        val uniqueUsersId: String
)