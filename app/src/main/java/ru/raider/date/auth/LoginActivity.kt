package ru.raider.date.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import ru.raider.date.MainActivity
import ru.raider.date.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref: SharedPreferences = getSharedPreferences("ru.raider.date.shared", Context.MODE_PRIVATE)
        val logged = sharedPref.getBoolean("LoggedIn", false)
        if (logged) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            setContentView(R.layout.activity_login)
        }
    }

    fun register(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun sign_in(view: View) {
        val user_name = loginEmail.text;
        val password = loginPassword.text;
        Toast.makeText(this@LoginActivity, user_name, Toast.LENGTH_LONG).show()

        val sharedPref: SharedPreferences = getSharedPreferences("ru.raider.date.shared", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean("LoggedIn", true)
            apply()
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // your code to validate the user_name and password combination
        // and verify the same
    }
}