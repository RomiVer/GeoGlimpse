package com.example.geoglimpse.data.api

import com.example.geoglimpse.data.CountryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("all")
    suspend fun getAllCountries(): Response<List<CountryResponse>>

    @GET("name/{name}")
    suspend fun getCountryByName(@Path("name") name: String): Response<List<CountryResponse>>

    @GET("region/{region}")
    suspend fun getCountryByRegion(@Path("region") region: String): Response<List<CountryResponse>>

    @GET("lang/{lang}")
    suspend fun getCountryByLanguage(@Path("lang") language: String): Response<List<CountryResponse>>

}
