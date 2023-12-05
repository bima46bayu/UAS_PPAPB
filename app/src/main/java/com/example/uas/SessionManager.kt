package com.example.uas

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "app-pref"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val PREF_NAME = "app-pref"
        private const val PRIVATE_MODE = 0
        private const val KEY_IS_LOGIN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        // Tambahkan kunci lainnya sesuai kebutuhan
    }

    fun setLogin(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGIN, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGIN, false)
    }

    fun saveUserData(username: String, email: String) {
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        // Tambahkan penyimpanan data lainnya sesuai kebutuhan
        editor.apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }
}
