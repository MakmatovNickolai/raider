package ru.raider.date.activities

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import isValidEmail
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.main_action_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.fragments.ProfileEditPhotosFragment
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.LoginResponse
import ru.raider.date.network_models.User
import ru.raider.date.utils.SessionManager
import sha256
import validate
import java.io.File
import java.io.FileNotFoundException
import java.util.*


class SignupActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private val apiClient = RaiderApiClient()
    private val pickImage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sessionManager = SessionManager(this)

        isToolbarTitle.text = "RAIDER"
        setSupportActionBar(includeToolbarSignUp as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val list = listOf<String>("Женщина", "Мужчина")
        idUserGender.adapter = ArrayAdapter<String>(this, R.layout.spinner_gender_item, list)
    }


    fun performSignUp(view: View) {
        if (
            idUserName.validate("Имя не должно быть пустым") { s -> !s.isNullOrEmpty()} &&
            idUserAge.validate("Возраст должен быть от 18 до 100") { s -> s.toIntOrNull() != null && s.toInt() >= 18  && s.toInt() <=100} &&
            idUserEmail.validate("Почта имеет неверный формат") { s -> s.isValidEmail() } &&
            idUserPassword.validate("Пароль должен состоять минимум из 6 символов") { s -> s.length >= MIN_PASSWORD_LENGTH })
        {
            val name = idUserName.text.toString()
            val city = idUserCity.text.toString()
            val gender = idUserGender.selectedItem.toString()
            val password = idUserPassword.text.toString().sha256()
            val email = idUserEmail.text.toString()
            val age = idUserAge.text.toString().toInt()
            val description = idUserDescription.text.toString()
            val user = User(
                    email = email,
                    password = password,
                    name = name,
                    age = age,
                    city = city,
                    sex = gender,
                    description = description
            )
            App.user = user

            // запустить фрагмент для добавления фото
            val profileEditPhotosFragment = ProfileEditPhotosFragment.newInstance()
            if (!profileEditPhotosFragment.isInLayout) {
                supportFragmentManager
                        .beginTransaction()
                        .replace(
                                R.id.idSignUpActivity, profileEditPhotosFragment,
                                "ProfileEditPhotosFragment"
                        )
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

    fun applyPhotoEditChanges(view: View) {
        apiClient.getApiService(this).signUp(App.user).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.i("DEV", "onFailure signup")
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val signUpResponse = response.body()
                if (signUpResponse != null) {
                    if (signUpResponse.error == "" && signUpResponse.authToken != "") {
                        sessionManager.setSharedPrefString("USER_TOKEN", signUpResponse.authToken)
                        sessionManager.setSharedPrefString("USER_HASH", signUpResponse.userRandomHash)
                        // добавляем пользователя глобально, чтобы получать к нему доступ везде в приложении
                        App.user = signUpResponse.user
                        val intent =
                                Intent(this@SignupActivity, MainActivity::class.java)
                        intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                        startActivity(intent)
                    } else {
                        Log.i("DEV", "onResponse " + signUpResponse.error)
                    }
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
    }
}