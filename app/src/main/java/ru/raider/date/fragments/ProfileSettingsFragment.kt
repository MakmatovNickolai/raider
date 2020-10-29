package ru.raider.date.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.raider.date.R

class ProfileSettingsFragment: Fragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_settings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            (activity as AppCompatActivity).supportActionBar?.hide()
        }
    }
    companion object {

        fun newInstance(): ProfileSettingsFragment {
            return ProfileSettingsFragment()
        }
    }

    fun applySettingsChanges(view:View) {
        // применить изменения в профиле
        // TODO: 29.10.2020
    }


}