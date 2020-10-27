package ru.raider.date.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import ru.raider.date.network.RaiderApiClient
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.models.LoginRequest
import ru.raider.date.models.LoginResponse
import ru.raider.date.utils.SessionManager
import sha256

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: RaiderApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        if (sessionManager.getSharedPrefString("USER_TOKEN").isNullOrEmpty()) {
            setContentView(R.layout.activity_login)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    fun register(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun sign_in(view: View) {
        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString().sha256()
        val user =
            LoginRequest(email = email, password = password)

        apiClient = RaiderApiClient()

        apiClient.getApiService(this).signIn(user).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity,"KAL ошибка в запросе",Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    if (loginResponse.error == "") {
                        sessionManager.setSharedPrefString("USER_TOKEN", loginResponse.authToken)
                        sessionManager.setSharedPrefString("USER_HASH", loginResponse.userRandomHash)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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