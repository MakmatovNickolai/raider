package ru.raider.date.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.raider.date.Profile
import ru.raider.date.auth.*

interface RaiderApiService {

    @GET("profiles")
    fun getProfiles(): Call<List<Profile>>

    @POST("https://raiderapi.herokuapp.com/signup")
    fun signup(@Body user: SignupRequest): Call<SignupResponse>

    @POST("https://raiderapi.herokuapp.com/signin")
    fun signin(@Body user: LoginRequest): Call<LoginResponse>

    @GET("https://raiderapi.herokuapp.com/like")
    fun like(@Query("id") id: String): Call<SimpleResponse>

}