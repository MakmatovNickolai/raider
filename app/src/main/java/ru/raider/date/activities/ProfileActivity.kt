package ru.raider.date.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import convertImageUrl
import kotlinx.android.synthetic.main.activity_profile.*
import ru.raider.date.R
import ru.raider.date.fragments.ProfileEditPhotosFragment
import ru.raider.date.fragments.ProfileSettingsFragment
import ru.raider.date.fragments.ProfileViewPhotosFragment
import ru.raider.date.network_models.User


class ProfileActivity : AppCompatActivity() {

    private lateinit var myProfile: User
    val images = listOf<String>(
        "https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/f4b9d68ae31fc834dc25811867fd2049ffed5810a9a74e8390710a39ed6068b0.jpg",
        "https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/88571be52a03b7fef4301def71017ecb785d6480082b5ed9dd83fc5898f5ac19.jpg",
        "https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/f636a5e41467e9101b9cbe1ba67f3edd51a697bd6f4ed9d503853986cb8ca5b1.jpg",
        "https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/f4b9d68ae31fc834dc25811867fd2049ffed5810a9a74e8390710a39ed6068b0.jpg"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        if (savedInstanceState == null) {
            myProfile = intent.getParcelableExtra("user")!!
            val newUrl = convertImageUrl(myProfile.pictureUrl)
            Picasso.get().load(newUrl).into(idProfilePhoto)
            idProfileName.text = myProfile.name
            idProfileAge.text = myProfile.age.toString()
            idProfileSex.text = myProfile.sex
            idProfilePhoto.setOnClickListener {
                // your code to perform when the user clicks on the ImageView
                //val profilePhotosFragment = ProfilePhotosFragment.newInstance(myProfile.photoUrls)


                val profilePhotosFragment = ProfileViewPhotosFragment.newInstance(images)
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

            // доделать тему https://gist.github.com/aqua30/e8623abaff190ee86727ee5ae8dac82a
            // TODO: 29.10.2020
            tv_heading.setAlpha(0f);
            /* set the scroll change listener on scrollview */
            idProfileInfoScrollView.getViewTreeObserver()
                .addOnScrollChangedListener(
                    OnScrollChangedListener { /* get the maximum height which we have scroll before performing any action */
                        val maxDistance: Int = idProfilePhoto.getHeight()
                        /* how much we have scrolled */
                        val movement: Int = idProfileInfoScrollView.getScrollY()
                        /*finally calculate the alpha factor and set on the view */
                        val alphaFactor: Float =
                            movement * 1.0f / (maxDistance - tv_heading.getHeight())
                        if (movement >= 0 && movement <= maxDistance) {
                            /*for image parallax with scroll */
                            idProfilePhoto.setTranslationY((-movement / 2).toFloat())
                            /* set visibility */
                            tv_heading.setAlpha(alphaFactor)
                        }
                    })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.profile_settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.idChangeProfileNameMenuItem -> {
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
                val profileEditPhotosFragment = ProfileEditPhotosFragment.newInstance(images)
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
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val PROFILE_VIEW_PHOTOS_FRAGMENT_TAG = "ProfileViewPhotosFragment"
        private const val PROFILE_SETTINGS_FRAGMENT_TAG = "ProfileSettingsFragment"
        private const val PROFILE_EDIT_PHOTOS_FRAGMENT_TAG = "ProfileEditPhotosFragment"
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.i("Dev", "popping backstack")
            if (supportActionBar?.isShowing == false) {
                supportActionBar?.show()
            }
            supportFragmentManager.popBackStack()
        } else {
            Log.i("Dev", "nothing on backstack, calling super")
            super.onBackPressed()
        }
    }
}

