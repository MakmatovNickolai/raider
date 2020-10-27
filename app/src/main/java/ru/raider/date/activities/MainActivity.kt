package ru.raider.date.activities


import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import ru.raider.date.R
import ru.raider.date.fragments.ExploreFragment
import ru.raider.date.fragments.MessagesFragment
import ru.raider.date.fragments.TestFragment


class MainActivity : AppCompatActivity() {
    private lateinit var exploreFragment: ExploreFragment
    private lateinit var testFragment: TestFragment
    private lateinit var messagesFragment: MessagesFragment
    private val MESSAGES_FRAGMENT_TAG = "MessagesFragment"
    private val TEST_FRAGMENT_TAG = "TestFragment"
    private val EXPLORE_FRAGMENT_TAG = "ExploreFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            bottomNavigationMenu.selectedItemId = R.id.navigation_explore;
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                    .show()
            R.id.action_settings -> Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                    .show()
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
                openFragment(exploreFragment, EXPLORE_FRAGMENT_TAG)
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_messages -> {
                if (!this::messagesFragment.isInitialized) {
                    Log.i("DEV", "isNotInitialized")
                    messagesFragment = MessagesFragment.newInstance()
                }

                openFragment(messagesFragment, MESSAGES_FRAGMENT_TAG)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_test -> {
                if (!this::testFragment.isInitialized) {
                    Log.i("DEV", "isNotInitialized")
                    testFragment = TestFragment.newInstance()
                }
                openFragment(testFragment, TEST_FRAGMENT_TAG)

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
                    .commit();
        }
    }
}