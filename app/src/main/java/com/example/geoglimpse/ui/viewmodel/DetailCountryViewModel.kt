package com.example.geoglimpse.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.geoglimpse.data.CountryResponse
import java.text.DecimalFormat

class DetailCountryViewModel : ViewModel() {
    var country: CountryResponse = CountryResponse()

    val capitalString: String
        get() {
            return country.capital?.first() ?: ""
        }
    val populationString: String
        get() {
            return DecimalFormat().format(country.population) ?: ""
        }
    val currenciesString: String
        get() {
            return country.currencies?.map { it.value.name }?.joinToString(", ") ?: ""
        }
    val regionString: String
        get() {
            return country.region ?: ""
        }
    val subregionString: String
        get() {
            return country.subregion ?: ""
        }
    val languagesString: String
        get() {
            return country.languages?.map { it.value }?.joinToString(", ") ?: ""
        }
    val bordersString: String
        get() {
            return country.borders?.joinToString(", ") ?: ""
        }
}