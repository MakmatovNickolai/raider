package ru.raider.date.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.profile_images_fragment.*
import ru.raider.date.R
import ru.raider.date.adapters.ViewPagerAdapter

class ProfileViewPhotosFragment: Fragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_images_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            (activity as AppCompatActivity).supportActionBar?.hide()
            val images = arguments?.getStringArray(PHOTO_URLS)
            idProfilePhotosViewPager.adapter = ViewPagerAdapter(activity?.applicationContext!!, images?.toList()!!)
            val indicator = idProfilePhotosIndicator
            indicator.setViewPager(idProfilePhotosViewPager)
        }
    }
    companion object {
        private const val PHOTO_URLS = "PHOTO_URLS"
        fun newInstance(images:List<String>): ProfileViewPhotosFragment {
            val fragment = ProfileViewPhotosFragment()
            val bundle = Bundle().apply {
                putStringArray(PHOTO_URLS, images.toTypedArray())
            }
            fragment.arguments = bundle
            return fragment
        }
    }


}