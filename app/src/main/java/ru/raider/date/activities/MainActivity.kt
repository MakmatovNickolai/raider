package ru.raider.date.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.fragments.ExploreFragment
import ru.raider.date.fragments.MessagesFragment
import ru.raider.date.fragments.TestFragment
import ru.raider.date.network_models.SimpleResponse
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.User
import ru.raider.date.utils.SessionManager


class MainActivity : AppCompatActivity() {
    private lateinit var exploreFragment: ExploreFragment
    private lateinit var testFragment: TestFragment
    private lateinit var messagesFragment: MessagesFragment
    private lateinit var sessionManager: SessionManager
    lateinit var apiClient: RaiderApiClient
    lateinit var myProfile: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        apiClient = RaiderApiClient()
        sessionManager = SessionManager(this)
        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            val user = User("1", "xer", "xer2", 23,"https://raiders3225357-dev.s3.eu-central-1.amazonaws.com/public/f4b9d68ae31fc834dc25811867fd2049ffed5810a9a74e8390710a39ed6068b0.jpg",
                "female","","")
            myProfile = user
            //myProfile = intent.getParcelableExtra("user")!!
            bottomNavigationMenu.selectedItemId = R.id.navigation_explore
        }
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
                intent.putExtra("user", myProfile)
                startActivity(intent)
            }
            R.id.idLogOut -> {
                val builder = AlertDialog.Builder(this@MainActivity)
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
                                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@MainActivity, simpleResponse?.result, Toast.LENGTH_SHORT)
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
                if (!this::testFragment.isInitialized) {
                    Log.i("DEV", "isNotInitialized")
                    testFragment = TestFragment.newInstance()
                }
                openFragment(testFragment, Companion.TEST_FRAGMENT_TAG)

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment, tag:String) {
        // ... fragment lookup or instantation from above...
        // Always add a tag to a fragment being inserted into container
        if (!fragment.isInLayout) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .commit()
        }
    }

    companion object {
        private const val MESSAGES_FRAGMENT_TAG = "MessagesFragment"
        private const val TEST_FRAGMENT_TAG = "TestFragment"
        private const val EXPLORE_FRAGMENT_TAG = "ExploreFragment"
    }
}