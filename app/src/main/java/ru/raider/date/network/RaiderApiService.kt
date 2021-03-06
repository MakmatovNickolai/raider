package ru.raider.date.network

import retrofit2.Call
import retrofit2.http.*
import ru.raider.date.network_models.*

interface RaiderApiService {

    @GET("fetch_users")
    fun fetchUsers(): Call<FetchUserResponse>

    @POST("sign_up")
    fun signUp(@Body user: User): Call<LoginResponse>

    @POST("sign_in")
    fun signIn(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("sign_out")
    fun signOut(): Call<SimpleResponse>

    @GET("like")
    fun like(@Query("id") id: String, @Query("like") like: String): Call<SimpleResponse>

    @POST("send_message")
    fun sendMessage(@Query("socket_id") socket_id: String, @Body message: Message): Call<SimpleResponse>

    @GET("fetch_rooms")
    fun fetchRooms(): Call<FetchRoomsResponse>

    @GET("fetch_messages")
    fun fetchMessages(@Query("room_id") room_id: String): Call<List<Message>>

    @GET("get_matches")
    fun getMatches(@Query("type") type: String): Call<FetchUserResponse>

    @GET("create_room")
    fun createRoom(@Query("target_user_id") target_user_id: String): Call<SimpleResponse>

    @GET("update_location")
    fun updateLocation(@Query("long") long: Double, @Query("lat") lat: Double): Call<SimpleResponse>

    @POST("update_profile_info")
    fun update_profile_info(@Body user: User): Call<SimpleResponse>

    @GET("check_auth")
    fun checkAuth(): Call<CheckAuthReponse>

}