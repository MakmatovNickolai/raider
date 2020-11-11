package ru.raider.date.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_profile_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.SimpleResponse
import ru.raider.date.network_models.User


class ProfileSettingsFragment: Fragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {

            idEditUserName.setText(App.user.name)
            idEditUserAge.setText(App.user.age.toString())
            idEditUserCity.setText(App.user.city)
            val selectionIndex = if (App.user.sex == "Мужчина") 0 else 1
            idEditUserGender.setSelection(selectionIndex)
            idEditUserDescription.setText(App.user.description)

        }
    }
    companion object {

        fun newInstance(): ProfileSettingsFragment {
            return ProfileSettingsFragment()
        }
    }



}