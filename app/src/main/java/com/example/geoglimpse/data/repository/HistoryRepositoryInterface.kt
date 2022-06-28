package com.example.geoglimpse.data.repository

interface HistoryRepositoryInterface {
    fun getHistory(): List<String>
    fun addCountryHistory(visitedCountry: String)
}