package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class RoomRecord (
        @SerializedName("room")
        var room: Room?,
        @SerializedName("user")
        var user: User?,
        @SerializedName("last_message")
        var lastMessage: String?
)