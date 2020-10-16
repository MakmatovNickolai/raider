package ru.raider.date

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.raider.date.auth.User

interface RaiderAPI {

    @GET("profiles")
    fun getProfiles(): Call<List<Profile>>

    @POST("https://raiderapi.herokuapp.com/signup")
    fun signup(@Body user: User): Call<String>

    companion object {
        operator fun invoke(): RaiderAPI {
            return Retrofit.Builder()
                .baseUrl("https://api.simplifiedcoding.in/course-apis/tinder/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RaiderAPI::class.java)
        }
    }
}