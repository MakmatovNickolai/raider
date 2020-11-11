package ru.raider.date.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_action_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.fragments.ExploreFragment
import ru.raider.date.fragments.MatchesFragment
import ru.raider.date.fragments.MessagesFragment
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.SimpleResponse
import ru.raider.date.network_models.User
import ru.raider.date.utils.SessionManager


class MainActivity : AppCompatActivity() {
    private lateinit var exploreFragment: ExploreFragment
    private lateinit var matchesFragment: MatchesFragment
    private lateinit var messagesFragment: MessagesFragment

    lateinit var apiClient: RaiderApiClient
    private lateinit var locationManager:LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        //locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)


        // TODO: 11.11.2020 местоположение
        // TODO: 11.11.2020 всю загрузку разделить на страницы, по 10 профилей, по 10 бесед, 10 сообщений, и подгружать дальше при необходимости
        // TODO: 11.11.2020 сделать серверную часть 
        
        setContentView(R.layout.activity_main)
        apiClient = RaiderApiClient()
        isToolbarTitle.text = "RAIDER"
        setSupportActionBar(includeToolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {

            bottomNavigationMenu.selectedItemId = R.id.navigation_explore
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.i("DEV", location.latitude.toString())
            Log.i("DEV", location.longitude.toString())
            apiClient.getApiService(this@MainActivity).updateLocation(location.longitude, location.latitude).enqueue(object : Callback<SimpleResponse> {
                override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                    Log.i("DEV", call.toString())
                    Log.i("DEV", t.message.toString())

                }

                override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                    val simpleResponse = response.body()

                    if (simpleResponse?.result == "OK") {
                        Toast.makeText(this@MainActivity, "OK", Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        Toast.makeText(this@MainActivity, simpleResponse?.result, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
            })
            locationManager.removeUpdates(this)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.idActionProfile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("activityType", "usual")
                startActivity(intent)
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_explore -> {
                if (!this::exploreFragment.isInitialized) {
                    Log.i("DEV", "isNotInitialized")
                    exploreFragment = ExploreFragment.newInstance()
                }
                openFragment(exploreFragment, Companion.EXPLORE_FRAGMENT_TAG)
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_messages -> {
                if (!this::messagesFragment.isInitialized) {
                    Log.i("DEV", "isNotInitialized")
                    messagesFragment = MessagesFragment.newInstance()
                }

                openFragment(messagesFragment, Companion.MESSAGES_FRAGMENT_TAG)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_test -> {
                if (!this::matchesFragment.isInitialized) {
                    Log.i("DEV", "isNotInitialized")
                    matchesFragment = MatchesFragment.newInstance()
                }
                openFragment(matchesFragment, Companion.TEST_FRAGMENT_TAG)

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        // ... fragment lookup or instantation from above...
        // Always add a tag to a fragment being inserted into container
        if (!fragment.isInLayout) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.i("Dev", "popping backstack")
            if (supportActionBar?.isShowing == false) {
                supportActionBar?.show()
            }
            if (bottomNavigationMenu.visibility != View.VISIBLE) {
                bottomNavigationMenu.visibility = View.VISIBLE
            }

            supportFragmentManager.popBackStack()
        } else {
            Log.i("Dev", "nothing on backstack, calling super")
            super.onBackPressed()
        }
    }

    companion object {
        private const val MESSAGES_FRAGMENT_TAG = "MessagesFragment"
        private const val TEST_FRAGMENT_TAG = "TestFragment"
        private const val EXPLORE_FRAGMENT_TAG = "ExploreFragment"
    }
}