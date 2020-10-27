package ru.raider.date.models

import com.google.gson.annotations.SerializedName

data class FetchUserResponse (
        @SerializedName("err")
        var err: String,
        @SerializedName("result")
        val result: MutableList<Profile>
)