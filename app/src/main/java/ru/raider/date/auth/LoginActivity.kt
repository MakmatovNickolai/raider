package ru.raider.date.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import ru.raider.date.api.RaiderApiClient
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.MainActivity
import ru.raider.date.R

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: RaiderApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        if (sessionManager.fetchAuthToken().isNullOrEmpty()) {
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
        val password = loginPassword.text.toString()
        val user =
            LoginRequest(email = email, password = password)

        apiClient = RaiderApiClient()

        apiClient.getApiService(this).signin(user).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity,"KAL ошибка в запросе",Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    if (loginResponse.error.equals("")) {
                        sessionManager.saveAuthToken(loginResponse.authToken)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, loginResponse.error, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}