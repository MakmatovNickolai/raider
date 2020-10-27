package ru.raider.date.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.raider.date.utils.AuthInterceptor

class RaiderApiClient {
    private lateinit var apiService: RaiderApiService

    fun getApiService(context: Context): RaiderApiService {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                    .client(okhttpClient(context))
                    .baseUrl("http://localhost:5000/")
                    // .baseUrl("https://raiderapi.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            apiService = retrofit.create(RaiderApiService::class.java)
        }

        return apiService
    }

    /**
     * Initialize OkhttpClient with our interceptor
     */
    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()
    }

}