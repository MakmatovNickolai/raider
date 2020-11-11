package ru.raider.date.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.main_action_bar.*
import retrofit2.Call
import ru.raider.date.network.RaiderApiClient
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.network_models.CheckAuthReponse
import ru.raider.date.network_models.LoginRequest
import ru.raider.date.network_models.LoginResponse
import ru.raider.date.network_models.SimpleResponse
import ru.raider.date.utils.SessionManager
import sha256
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: RaiderApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_login)
        isToolbarTitle.text = "RAIDER"
        setSupportActionBar(includeToolbarLogin as Toolbar)
        sessionManager = SessionManager(this)
        apiClient = RaiderApiClient()


    }


    fun register(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun signIn(view: View) {
        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString().sha256()
        val user = LoginRequest(email = email, password = password)

        apiClient.getApiService(this).signIn(user).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity,  t.localizedMessage + " " + t.message ,Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    if (loginResponse.error == "") {
                        sessionManager.setSharedPrefString("USER_TOKEN", loginResponse.authToken)
                        sessionManager.setSharedPrefString("USER_HASH", loginResponse.userRandomHash)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                        // добавляем пользователя глобально, чтобы получать к нему доступ везде в приложении
                        App.user = loginResponse.user
                        App.user.pictureUrls?.sortByDescending{it == App.user.main_picture_url}
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, loginResponse.error, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Ошибка" + response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}