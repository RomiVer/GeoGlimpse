package com.example.geoglimpse.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryResponse(
    var name: Name = Name(),
    var currencies: Map<String, Currency>? = null,
    var capital: List<String>? = null,
    var region: String? = null,
    var subregion: String? = null,
    var languages: Map<String, String>? = null,
    var latlng: List<Double>? = null,
    val borders: List<String>? = null,
    val area: Double? = null,
    val population: Long? = null,
    val flags: Image? = null,
    var selected: Boolean = false,
    var visited: Boolean = false
) : Parcelable

@Parcelize
data class Name(
    var common: String = "",
    var official: String = "",
    var nativeName: Map<String, Name>? = mapOf()
) : Parcelable

@Parcelize
data class Currency(
    val name: String,
    val symbol: String
) : Parcelable

@Parcelize
data class Image(
    val png: String = "",
    val svg: String = ""
) : Parcelable
