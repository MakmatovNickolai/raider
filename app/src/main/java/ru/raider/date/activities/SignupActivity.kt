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
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import isValidEmail
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.SignupResponse
import ru.raider.date.network_models.User
import ru.raider.date.utils.SessionManager
import sha256
import validate
import java.io.File
import java.io.FileNotFoundException
import java.util.*


class SignupActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: RaiderApiClient
    private val pickImage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val list = listOf<String>("Женский", "Мужской")
        idUserGender.adapter = ArrayAdapter<String>(this, R.layout.spinner_gender_item, list)
        // TODO: 30.10.2020 переделать дизайн для страницы регистрации 
    }


    fun performSignUp(view: View) {
        if (
            idUserName.validate("Имя не должно быть пустым") { s -> !s.isNullOrEmpty()} &&
            idUserAge.validate("Возраст должен быть от 18 до 100") { s -> s.toIntOrNull() != null && s.toInt() >= 18  && s.toInt() <=100} &&
            idUserEmail.validate("Почта имеет неверный формат") { s -> s.isValidEmail() } &&
            idUserPassword.validate("Пароль должен состоять минимум из 6 символов") { s -> s.length >= MIN_PASSWORD_LENGTH })
        {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImage)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            pickImage -> if (resultCode == Activity.RESULT_OK) {
                try {
                    val imageUri: Uri? = imageReturnedIntent?.data
                    val path = getPath(imageUri!!)
                    val selectedImageFile = File(path)
                    val email = idUserEmail.text.toString()
                    var uniqueId = (UUID.randomUUID().toString() + email).sha256()

                    val options = StorageUploadFileOptions.builder()
                        .accessLevel(StorageAccessLevel.PUBLIC)
                        .build()


                    val name = idUserName.text.toString()
                    val city = idUserCity.text.toString()
                    val gender = idUserGender.selectedItem.toString()
                    val password = idUserPassword.text.toString().sha256()
                    val age = idUserAge.text.toString().toInt()

                    // TODO: 30.10.2020 при нажатии зарегаться открыть фрагмент с добавлением фото, затем эти фото прикрепить к юзеру в profileUrls. в юзере тоже поменять url на массив url-ов
                    //val user = User(id=userId, email=email, password = password, name=name, surname = surname, age=age, picture_url="https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/" + it.key ,sex="female")
                    val user = User(
                        email = email, password = password, name = name, age = age, city = city,
                        pictureUrl = "https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/88571be52a03b7fef4301def71017ecb785d6480082b5ed9dd83fc5898f5ac19.jpg",
                        sex = gender
                    )

                    apiClient = RaiderApiClient()
                    sessionManager = SessionManager(this)

                    apiClient.getApiService(this).signUp(user)
                        .enqueue(object : Callback<SignupResponse> {
                            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                                Toast.makeText(
                                    this@SignupActivity,
                                    "KAL ошибка в запросе",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<SignupResponse>,
                                response: Response<SignupResponse>
                            ) {
                                Log.i(
                                    "MyAmplifyApp",
                                    "Successfully Tyt"
                                )
                                val signupResponse = response.body()
                                if (signupResponse != null) {
                                    if (signupResponse.error == "" && signupResponse.authToken != "") {
                                        sessionManager.setSharedPrefString(
                                            "USER_TOKEN",
                                            signupResponse.authToken
                                        )
                                        sessionManager.setSharedPrefString(
                                            "USER_HASH",
                                            signupResponse.userRandomHash
                                        )
                                        val intent =
                                            Intent(this@SignupActivity, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        // TODO: 30.10.2020 отправить местоположение после логина
                                        startActivity(intent)

                                    } else {
                                        Toast.makeText(
                                            this@SignupActivity,
                                            "KAL " + response.body(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        })
/*
                    Amplify.Storage.uploadFile(
                            "$uniqueID.jpg",
                            selectedImageFile,
                            options,
                            {
                                Log.i(
                                        "MyAmplifyApp",
                                        "Successfully uploaded: " + it.key
                                )


                            },
                            { error -> Log.e("MyAmplifyApp", "Upload failed", error) }
                    )

 */
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getPath(imageUri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
                contentResolver.query(imageUri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
    }
}