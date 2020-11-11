package ru.raider.date.activities

import android.R.attr.data
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso
import convertImageUrl
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_profile_edit_photos.*
import kotlinx.android.synthetic.main.fragment_profile_settings.*
import kotlinx.android.synthetic.main.main_action_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.App
import ru.raider.date.App.Companion.user
import ru.raider.date.R
import ru.raider.date.fragments.ProfileEditPhotosFragment
import ru.raider.date.fragments.ProfileSettingsFragment
import ru.raider.date.fragments.ProfileViewPhotosFragment
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.SimpleResponse
import ru.raider.date.network_models.User
import ru.raider.date.utils.SessionManager
import validate


class ProfileActivity : AppCompatActivity() {
    private var profileUser:User? = null
    private var apiClient = RaiderApiClient()
    private lateinit var sessionManager: SessionManager
    private lateinit var profileMenu:Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        if (savedInstanceState == null) {
            sessionManager = SessionManager(this)

            // TODO: 11.11.2020 переделать скроллинг и кнопки 
            // 10.11.2020 переделать кнопки лайк, не лайк, начать диалог -- без material некруто, а переходить на материал сложно кажется пока что
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

            profileUser = intent.getParcelableExtra<User>("user")
            Log.i("DEV", profileUser.toString())

            if (profileUser == null) {
                profileUser = App.user
                isToolbarTitle.text = "Мой профиль"
            } else {
                isToolbarTitle.text = "Профиль"
            }
            setSupportActionBar(includeToolbarProfile as Toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            val activityType = intent.getStringExtra("activityType")
            if (!activityType.isNullOrEmpty()) {
                when (activityType) {
                    "LikedMe" -> {
                        LikedMeLayoutButtons.visibility = View.VISIBLE
                    }
                    "LikedBoth" -> {
                        LikedBothLayoutButtons.visibility = View.VISIBLE
                    }
                }
            }

            try {
                setProfilePicture()
            }
            catch (ex: NoSuchElementException) {
                idProfilePhoto.setImageResource(R.drawable.ic_baseline_person_24)
            }

            idprofileUserName.text = profileUser?.name
            idProfileUserMainInfo.text = "${profileUser?.age} / ${profileUser?.sex} / ${profileUser?.city}"
            idProfileUserDescription.text = profileUser?.description

            idProfileInfoLayout.setOnClickListener {
                // your code to perform when the user clicks on the ImageView
                //val profilePhotosFragment = ProfilePhotosFragment.newInstance(myProfile.photoUrls)
                if (activityType == "usual") {
                    profileUser = App.user
                }

                val profilePhotosFragment = ProfileViewPhotosFragment.newInstance(profileUser!!)
                if (!profilePhotosFragment.isInLayout) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                                R.id.idMainFrame, profilePhotosFragment,
                                PROFILE_VIEW_PHOTOS_FRAGMENT_TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setProfilePicture() {
        val newUrl = convertImageUrl(profileUser?.main_picture_url)
        Picasso.get().load(newUrl).into(idProfilePhoto)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (profileUser == App.user) {
            inflater.inflate(R.menu.profile_settings_menu, menu)
            profileMenu = menu
        }


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.idChangeProfileNameMenuItem -> {
                profileMenu.setGroupVisible(R.id.idProfileMenuItemsGroup, false)
                val profileSettingsFragment = ProfileSettingsFragment.newInstance()
                if (!profileSettingsFragment.isInLayout) {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(
                                    R.id.idMainFrame, profileSettingsFragment,
                                    PROFILE_SETTINGS_FRAGMENT_TAG
                            )
                            .addToBackStack(null)
                            .commit()
                }
            }
            R.id.idChangeProfilePhotosMenuItem -> {
                profileMenu.setGroupVisible(R.id.idProfileMenuItemsGroup, false)
                val profileEditPhotosFragment = ProfileEditPhotosFragment.newInstance()
                if (!profileEditPhotosFragment.isInLayout) {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(
                                    R.id.idMainFrame, profileEditPhotosFragment,
                                    PROFILE_EDIT_PHOTOS_FRAGMENT_TAG
                            )
                            .addToBackStack(null)
                            .commit()
                }
            }
            R.id.idLogOut -> {
                val builder = AlertDialog.Builder(this@ProfileActivity)
                builder.setMessage("Уверен нет, что хочешь выйти?")
                    .setCancelable(false)
                    .setNegativeButton("Нулячий пока") { dialog, _ ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                    .setPositiveButton("Да, прикинь") { _, _ ->
                        // Delete selected note from database
                        apiClient.getApiService(this).signOut().enqueue(object : Callback<SimpleResponse> {
                            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                                Log.i("DEV", call.toString())
                                Log.i("DEV", t.message.toString())

                            }

                            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                                val simpleResponse = response.body()
                                if (simpleResponse?.result == "OK") {
                                    sessionManager.deleteAuthStrings()
                                    val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@ProfileActivity, simpleResponse?.result, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        })
                    }

                val alert = builder.create()
                alert.show()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun applySettingsChanges(view: View) {
        if (!idEditUserName.validate("Имя не должно быть пустым") { s -> !s.isNullOrEmpty()} ||
            !idEditUserAge.validate("Возраст должен быть от 18 до 100") { s -> s.toIntOrNull() != null && s.toInt() >= 18  && s.toInt() <=100})
        {
            return
        }
        App.user.name = idEditUserName.text.toString()
        App.user.age = idEditUserAge.text.toString().toInt()
        App.user.city = idEditUserCity.text.toString()
        App.user.sex = idEditUserGender.selectedItem.toString()
        App.user.description = idEditUserDescription.text.toString()

        idApplyProfileSettingChangesButton.isEnabled = false
        updateProfile()
    }

    fun applyPhotoEditChanges(view: View) {
        idApplyPhotoEditChanges.isEnabled = false
        updateProfile()
    }

    private fun updateProfile() {

        val apiClient = RaiderApiClient()
        apiClient.getApiService(this).update_profile_info(App.user).enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(
                call: Call<SimpleResponse>,
                t: Throwable
            ) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())

            }

            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                val simpleResponse = response.body()
                Log.i("DEV", simpleResponse.toString())
                onBackPressed()
            }
        })
    }

    fun onLikeClicked(view: View) {
        like("1")
    }
    fun onPassClicked(view: View) {
        like("0")
    }

    fun like(type: String) {
        button2.isEnabled = false
        button3.isEnabled = false
        apiClient.getApiService(this).like(profileUser?.id!!, type).enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())

            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                val simpleResponse = response.body()
                val intent = Intent()
                intent.putExtra("user", profileUser)
                setResult(Activity.RESULT_OK, intent)
                onBackPressed()
            }
        })
    }

    fun onStartDialogClicked(view: View) {
        button4.isEnabled = false
        apiClient.getApiService(this).createRoom(profileUser?.id!!).enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())

            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                val simpleResponse = response.body()
                if (simpleResponse != null) {
                    Log.i("DEV", simpleResponse.toString())
                    if (!simpleResponse.result.isNullOrEmpty()) {
                        val intent = Intent()
                        intent.putExtra("user", profileUser)
                        intent.putExtra("roomId", simpleResponse.result)
                        setResult(Activity.RESULT_OK, intent)
                        onBackPressed()
                    }

                } else {

                }
            }
        })
    }

    companion object {
        private const val PROFILE_VIEW_PHOTOS_FRAGMENT_TAG = "ProfileViewPhotosFragment"
        private const val PROFILE_SETTINGS_FRAGMENT_TAG = "ProfileSettingsFragment"
        private const val PROFILE_EDIT_PHOTOS_FRAGMENT_TAG = "ProfileEditPhotosFragment"
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.i("DEV", "popBackStack")
/*
            if (supportActionBar?.isShowing == false) {
                supportActionBar?.show()
            }
            // хрень
            profileMenu.setGroupVisible(R.id.idProfileMenuItemsGroup, true)
            profileUser = App.user
            setProfilePicture()


 */

            supportFragmentManager.popBackStack()
            finish()
            startActivity(intent)

        } else {
            Log.i("DEV", "onBackPressed")

            super.onBackPressed()

        }
    }
}

