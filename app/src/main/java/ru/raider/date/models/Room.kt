package ru.raider.date.models

import com.google.gson.annotations.SerializedName

data class Room (
        @SerializedName("id")
        val id: String,
        @SerializedName("uniqueUsersId")
        val uniqueUsersId: String
)