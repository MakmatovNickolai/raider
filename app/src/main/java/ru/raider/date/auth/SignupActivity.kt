package ru.raider.date.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.MainActivity
import ru.raider.date.R
import ru.raider.date.api.RaiderApiClient
import sha256

class SignupActivity : AppCompatActivity() {

    private val MIN_PASSWORD_LENGTH = 6
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: RaiderApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Checking if the input in form is valid
    private fun validateInput(): Boolean {
        if (et_first_name.text.toString().equals("")) {
            et_first_name.error = "Имя введи"
            return false
        }
        if (et_last_name.text.toString().equals("")) {
            et_last_name.error = "Фамилию введи"
            return false
        }
        if (et_email.text.toString().equals("")) {
            et_email.error = "Почту введи"
            return false
        }
        if (et_age.text.toString().toInt() < 18 ||
                et_age.text.toString().toInt() > 100) {
            et_age.error = "Возраст от 18 до 100"
            return false
        }
        if (et_password.text.toString().equals("")) {
            et_password.error = "Надо ввести пароль"
            return false
        }

        // checking the proper email format
        if (!isEmailValid(et_email.text.toString())) {
            et_email.error = "Почта кал"
            return false
        }

        // checking minimum password Length
        if (et_password.text.length < MIN_PASSWORD_LENGTH) {
            et_password.error = "В пароле должно быть не менее " + MIN_PASSWORD_LENGTH + "знаков"
            return false
        }

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Hook Click Event

    fun performSignUp (view: View) {
        if (validateInput()) {

            // Input is valid, here send data to your server

            val firstName = et_first_name.text.toString()
            val lastName = et_last_name.text.toString()
            val email = et_email.text.toString()
            val password = et_password.text.toString().sha256()
            val age = et_age.text.toString().toInt()
            val user = SignupRequest(firstName, lastName, email, password, age)

            apiClient = RaiderApiClient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).signup(user).enqueue(object : Callback<SignupResponse> {
                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    Toast.makeText(this@SignupActivity,"KAL ошибка в запросе",Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if (loginResponse.error == "" && loginResponse.authToken != "") {
                            sessionManager.saveAuthToken(loginResponse.authToken)
                            val intent = Intent(this@SignupActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SignupActivity,"KAL " + response.body(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }
}