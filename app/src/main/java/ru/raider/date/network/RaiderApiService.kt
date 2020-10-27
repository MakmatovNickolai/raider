package ru.raider.date.network

import retrofit2.Call
import retrofit2.http.*
import ru.raider.date.models.*

interface RaiderApiService {

    @GET("fetch_users")
    fun fetchUsers(): Call<FetchUserResponse>

    @POST("signup")
    fun signUp(@Body user: User): Call<SignupResponse>

    @POST("signin")
    fun signIn(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("like")
    fun like(@Query("id") id: String, @Query("like") like: String): Call<SimpleResponse>

    @POST("send_message")
    fun sendMessage(@Body message: Message): Call<SimpleResponse>

    @GET("fetch_rooms")
    fun fetchRooms(): Call<FetchRoomsResponse>

    @GET("fetch_messages")
    fun fetchMessages(@Query("room_id") room_id: String): Call<List<Message>>

    @GET("get_matches")
    fun getMatches(@Query("type") type: String): Call<FetchUserResponse>

    @GET("create_room")
    fun createRoom(@Query("target_user_id") target_user_id: String): Call<SimpleResponse>

}