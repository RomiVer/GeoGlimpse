package com.example.geoglimpse.data.repository

import android.content.Context
import com.example.geoglimpse.data.repository.PreferencesHelper.countries
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesRepository(private val context: Context) : HistoryRepositoryInterface {
    private val gson: Gson by lazy { Gson() }
    private var visitedCountriesList: MutableList<String>? = null

    override fun getHistory(): List<String> {
        val countriesString = PreferencesHelper.defaultPreference(context).countries
        val countryType = object : TypeToken<List<String>>() {}.type
        visitedCountriesList = gson.fromJson(countriesString, countryType) ?: mutableListOf()
        return visitedCountriesList as MutableList<String>
    }

    override fun addCountryHistory(visitedCountry: String) {
        if (visitedCountriesList == null) {
            getHistory()
        }
        visitedCountriesList?.let {
            if (!it.contains(visitedCountry)) {
                it.add(0, visitedCountry)
                if (it.size > 3) {
                    it.removeLast()
                }
            }
            val countriesString = gson.toJson(it)
            PreferencesHelper.defaultPreference(context).countries = countriesString
        }
    }
}
