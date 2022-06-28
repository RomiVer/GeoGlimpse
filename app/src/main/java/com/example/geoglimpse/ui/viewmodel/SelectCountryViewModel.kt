package com.example.geoglimpse.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geoglimpse.data.CountryResponse
import com.example.geoglimpse.data.models.SearchType
import com.example.geoglimpse.data.repository.HistoryRepositoryInterface
import com.example.geoglimpse.data.repository.MainRepository
import kotlinx.coroutines.*

class SelectCountryViewModel(
    private val repository: MainRepository,
    private val historyRepository: HistoryRepositoryInterface
) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    private val _countryList = MutableLiveData<List<CountryResponse>>()
    val countryList: LiveData<List<CountryResponse>> get() = _countryList
    private val _selectedCountry = MutableLiveData<CountryResponse?>()
    val selectedCountry: LiveData<CountryResponse?> get() = _selectedCountry
    var searchBy: SearchType = SearchType.NAME
    var query: String? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun setSelectedCountry(selectedCountry: CountryResponse?) {
        _selectedCountry.value = selectedCountry
    }

    fun getAllCountries() {
        viewModelScope.launch {
            val response = repository.getAllCountries()
            if (response.isSuccessful) {
                response.body()?.sortedBy { it.name.common }?.toMutableList()?.let {
                    val fullList = buildCountryListWithHistory(it)
                    _countryList.postValue(fullList)
                }
            } else {
                onError("Error : ${response.message()} ")
            }
        }
    }

    private fun buildCountryListWithHistory(list: MutableList<CountryResponse>): List<CountryResponse> {
        var countryVisited: CountryResponse
        historyRepository.getHistory().reversed().forEach { history ->
            val index = list.indexOfFirst {
                it.name.common == history
            }
            if (index > 0) {
                countryVisited = list.removeAt(index)
                countryVisited.visited = true
                list.add(0, countryVisited)
            }
        }
        return list
    }

    fun getCountryByName(name: String) {
        viewModelScope.launch {
            val response = repository.getCountryByName(name)
            if (response.isSuccessful) {
                response.body()?.sortedBy { it.name.common }?.toMutableList()?.let {
                    val fullList = buildCountryListWithHistory(it)
                    _countryList.postValue(fullList)
                }
            } else {
                onError("Error : ${response.message()} ")
                _countryList.postValue(emptyList())
            }
        }
    }

    fun getCountryByRegion(region: String) {
        viewModelScope.launch {
            val response = repository.getCountryByRegion(region)
            if (response.isSuccessful) {
                response.body()?.sortedBy { it.name.common }?.toMutableList()?.let {
                    val fullList = buildCountryListWithHistory(it)
                    _countryList.postValue(fullList)
                }
            } else {
                onError("Error : ${response.message()} ")
                _countryList.postValue(emptyList())
            }
        }
    }

    fun getCountryByLanguage(language: String) {
        viewModelScope.launch {
            val response = repository.getCountryByLanguage(language)
            if (response.isSuccessful) {
                response.body()?.sortedBy { it.name.common }?.toMutableList()?.let {
                    val fullList = buildCountryListWithHistory(it)
                    _countryList.postValue(fullList)
                }
            } else {
                onError("Error : ${response.message()} ")
                _countryList.postValue(emptyList())
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
    }

    fun addCountryToHistory() {
        selectedCountry.value?.visited = true
        historyRepository.addCountryHistory(selectedCountry.value?.name?.common ?: "")
        _countryList.postValue(countryList.value?.toMutableList()
            ?.let { buildCountryListWithHistory(it) })
    }
}