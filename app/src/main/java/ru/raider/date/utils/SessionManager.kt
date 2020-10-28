package ru.raider.date.utils

import android.content.Context
import android.content.SharedPreferences
import ru.raider.date.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun setSharedPrefString(key:String, token: String) {
        val editor = prefs.edit()
        editor.putString(key, token)
        editor.apply()
    }

    fun getSharedPrefString(key:String): String? {
        return prefs.getString(key, null)
    }

    fun deleteAuthStrings() {
        val editor = prefs.edit()
        editor.remove("USER_TOKEN")
        editor.remove("USER_HASH")
        editor.apply()
    }
}