package ru.raider.date.network_models

import com.google.gson.annotations.SerializedName

data class FetchRoomsResponse (
        @SerializedName("err")
        var err: String,
        @SerializedName("result")
        val result: List<RoomRecord>
)