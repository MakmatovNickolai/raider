package ru.raider.date

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    lateinit var toolbar: ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        toolbar = supportActionBar!!
        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            bottomNavigationMenu.selectedItemId = R.id.navigation_explore;
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_explore -> {
                val exploreFragment = ExploreFragment.newInstance()
                openFragment(exploreFragment)
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_messages -> {
                val messagesFragment = MessagesFragment.newInstance()
                openFragment(messagesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_test -> {
                val profileFragment = TestFragment.newInstance()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}