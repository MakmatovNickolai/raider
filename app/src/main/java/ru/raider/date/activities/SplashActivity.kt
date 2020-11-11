package ru.raider.date.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.CheckAuthReponse
import ru.raider.date.utils.SessionManager
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: RaiderApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        sessionManager = SessionManager(this)
        apiClient = RaiderApiClient()
        Picasso.get().load("https://i.playground.ru/p/ivvW2FRAay_I4wD3jqShaw.png").into(idRaiderMainLogo,  object: com.squareup.picasso.Callback {
            override fun onSuccess() {
                if (sessionManager.getSharedPrefString("USER_TOKEN") != null) {
                    checkAuth()
                } else {
                    startLoginActivity()
                }

            }

            override fun onError(e: java.lang.Exception?) {
                //do smth when there is picture loading error
            }
        })

    }

    fun startMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun startLoginActivity() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun checkAuth() {
        apiClient.getApiService(this).checkAuth().enqueue(object : Callback<CheckAuthReponse> {
            override fun onFailure(call: Call<CheckAuthReponse>, t: Throwable) {
                Toast.makeText(this@SplashActivity,   t.message , Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            override fun onResponse(call: Call<CheckAuthReponse>, response: Response<CheckAuthReponse>) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    if (loginResponse.error == "") {
                        App.user = loginResponse.result
                        Collections.swap(App.user.pictureUrls, 0, App.user.main_picture_url!!.indexOf(App.user.main_picture_url!!))
                        startMainActivity()
                    } else {
                        if (loginResponse.error == "No auth user") {

                        } else {
                            Log.i("DEV",loginResponse.error )
                        }
                        startLoginActivity()
                    }
                } else {
                    Toast.makeText(this@SplashActivity, "Ошибка" + response.message(), Toast.LENGTH_SHORT).show()
                }

            }
        })
    }
}