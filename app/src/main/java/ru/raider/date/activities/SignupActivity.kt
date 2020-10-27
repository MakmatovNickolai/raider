package ru.raider.date.activities

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.utils.SessionManager
import ru.raider.date.models.SignupResponse
import ru.raider.date.models.User
import sha256
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class SignupActivity : AppCompatActivity() {

    private val MIN_PASSWORD_LENGTH = 6
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: RaiderApiClient
    val pickImage = 1
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

    fun performSignUp (view: View) {
        if (validateInput()) {

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
                    val email = et_email.text.toString()
                    var uniqueId = (UUID.randomUUID().toString() + email).sha256()

                    val options = StorageUploadFileOptions.builder()
                            .accessLevel(StorageAccessLevel.PUBLIC)
                            .build()


                    val name = et_first_name.text.toString()
                    val surname = et_last_name.text.toString()
                    val password = et_password.text.toString().sha256()
                    val age = et_age.text.toString().toInt()
                    var userId = UUID.randomUUID().toString()

                  //  val user = User(id=userId, email=email, password = password, name=name, surname = surname, age=age, picture_url="https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/" + it.key ,sex="female")
                    val user = User(id=userId, email=email, password = password, name=name, surname = surname, age=age, pictureUrl="https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/88571be52a03b7fef4301def71017ecb785d6480082b5ed9dd83fc5898f5ac19.jpg" ,sex="female")

                    apiClient = RaiderApiClient()
                    sessionManager = SessionManager(this)

                    apiClient.getApiService(this).signUp(user).enqueue(object : Callback<SignupResponse> {
                        override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                            Toast.makeText(this@SignupActivity,"KAL ошибка в запросе",Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                            Log.i(
                                    "MyAmplifyApp",
                                    "Successfully Tyt"
                            )
                            val signupResponse = response.body()
                            if (signupResponse != null) {
                                if (signupResponse.error == "" && signupResponse.authToken != "") {
                                    sessionManager.setSharedPrefString("USER_TOKEN", signupResponse.authToken)
                                    sessionManager.setSharedPrefString("USER_HASH", signupResponse.userRandomHash)
                                    val intent = Intent(this@SignupActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@SignupActivity,"KAL " + response.body(),Toast.LENGTH_SHORT).show()
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
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }
}