package ru.raider.date.utils

import android.content.Context
import android.content.SharedPreferences
import ru.raider.date.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    /**
     * Function to save auth token
     */
    fun setSharedPrefString(key:String, token: String) {
        val editor = prefs.edit()
        editor.putString(key, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun getSharedPrefString(key:String): String? {
        return prefs.getString(key, null)
    }
}