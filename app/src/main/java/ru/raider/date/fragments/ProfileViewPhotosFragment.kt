package ru.raider.date.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile_images.*
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.adapters.ViewPagerAdapter
import ru.raider.date.network_models.PictureUrl
import ru.raider.date.network_models.User
import java.util.*

class ProfileViewPhotosFragment: Fragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_images, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            (activity as AppCompatActivity).supportActionBar?.hide()
            hideBottomNavigationMenu()
            val images = arguments?.getStringArray(PHOTO_URLS)
            idProfilePhotosViewPager.adapter = ViewPagerAdapter(activity?.applicationContext!!, images?.toList())
            val indicator = idProfilePhotosIndicator
            indicator.setViewPager(idProfilePhotosViewPager)

        }
    }

    private fun hideBottomNavigationMenu() {
        if ((activity as AppCompatActivity).bottomNavigationMenu != null) {
            (activity as AppCompatActivity).bottomNavigationMenu.visibility = View.GONE
        }

    }
    companion object {
        private const val PHOTO_URLS = "PHOTO_URLS"
        fun newInstance(passedUser: User): ProfileViewPhotosFragment {
            val fragment = ProfileViewPhotosFragment()

            val index = passedUser.pictureUrls?.indexOf(passedUser.main_picture_url)
            if (index != 0) {
                Collections.swap(passedUser.pictureUrls, 0, index!!)
            }
            val bundle = Bundle().apply {
                putStringArray(PHOTO_URLS, passedUser.pictureUrls?.toTypedArray())
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}