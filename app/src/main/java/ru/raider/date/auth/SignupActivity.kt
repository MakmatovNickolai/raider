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
import ru.raider.date.MainActivity
import ru.raider.date.R

class SignupActivity : AppCompatActivity() {

    val MIN_PASSWORD_LENGTH = 6;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Checking if the input in form is valid
    fun validateInput(): Boolean {
        if (et_first_name.text.toString().equals("")) {
            et_first_name.setError("Please Enter First Name")
            return false
        }
        if (et_last_name.text.toString().equals("")) {
            et_last_name.setError("Please Enter Last Name")
            return false
        }
        if (et_email.text.toString().equals("")) {
            et_email.setError("Please Enter Email")
            return false
        }
        if (et_password.text.toString().equals("")) {
            et_password.setError("Please Enter Password")
            return false
        }
        if (et_repeat_password.text.toString().equals("")) {
            et_repeat_password.setError("Please Enter Repeat Password")
            return false
        }

        // checking the proper email format
        if (!isEmailValid(et_email.text.toString())) {
            et_email.setError("Please Enter Valid Email")
            return false
        }

        // checking minimum password Length
        if (et_password.text.length < MIN_PASSWORD_LENGTH) {
            et_password.setError("Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters")
            return false
        }

        // Checking if repeat password is same
        if (!et_password.text.toString().equals(et_repeat_password.text.toString())) {
            et_repeat_password.setError("Password does not match")
            return false
        }
        return true
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Hook Click Event

    fun performSignUp (view: View) {
        if (validateInput()) {

            // Input is valid, here send data to your server

            val firstName = et_first_name.text.toString()
            val lastName = et_last_name.text.toString()
            val email = et_email.text.toString()
            val password = et_password.text.toString()
            val repeatPassword = et_repeat_password.text.toString()

            Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show()
            // Here you can call you API\
            val sharedPref: SharedPreferences = getSharedPreferences("ru.raider.date.shared", Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putBoolean("LoggedIn", true)
                apply()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }

}