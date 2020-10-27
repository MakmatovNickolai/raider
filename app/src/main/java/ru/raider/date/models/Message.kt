package ru.raider.date.models

import com.google.gson.annotations.SerializedName

data class Message (
        @SerializedName("id")
        val id: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("from_user_id")
        val fromUserId: String,
        @SerializedName("room_id")
        val roomId: String
)