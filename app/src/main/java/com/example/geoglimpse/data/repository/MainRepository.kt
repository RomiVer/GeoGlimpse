package com.example.geoglimpse.data.repository

import com.example.geoglimpse.data.api.ApiService

class MainRepository(private val apiService: ApiService) {

    suspend fun getAllCountries() = apiService.getAllCountries()

    suspend fun getCountryByName(name: String) = apiService.getCountryByName(name)

    suspend fun getCountryByRegion(region: String) = apiService.getCountryByRegion(region)

    suspend fun getCountryByLanguage(language: String) = apiService.getCountryByLanguage(language)

}