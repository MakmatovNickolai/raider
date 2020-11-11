package ru.raider.date.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.raider.date.network_models.NoConnectivityException
import java.io.IOException


class NetworkConnectionInterceptor(context: Context?): Interceptor {
    private val ctx = context
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        if (!isConnected()) {
            throw NoConnectivityException()
            // Throwing our custom exception 'NoConnectivityException'
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isConnected(): Boolean {
        val connectivityManager = ctx?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.isDefaultNetworkActive
    }
}