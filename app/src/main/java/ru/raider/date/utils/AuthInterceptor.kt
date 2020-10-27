package ru.raider.date.utils

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import sha256


class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        sessionManager.getSharedPrefString("USER_TOKEN")?.let {
            request = request.newBuilder().addHeader("Authorization", "Bearer $it.").build()
        }

        sessionManager.getSharedPrefString("USER_HASH")?.let {
            val str = (it+"xer").sha256()
            request = request.newBuilder().url(request.url().newBuilder().addQueryParameter("user_random_hash", "$str").build()).build();
        }
        return chain.proceed(request)
    }
}