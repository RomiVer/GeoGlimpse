package com.example.geoglimpse.data.repository

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    private const val COUNTRY_LIST = "COUNTRIES"
    private const val APP_PREFS = "appPrefs"

    fun defaultPreference(context: Context): SharedPreferences = context.getSharedPreferences(
        APP_PREFS, Context.MODE_PRIVATE
    )

    var SharedPreferences.countries
        get() = getString(COUNTRY_LIST, "")
        set(value) {
            edit().putString(COUNTRY_LIST, value).apply()
        }
}