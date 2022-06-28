package com.example.geoglimpse.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geoglimpse.data.repository.HistoryRepositoryInterface
import com.example.geoglimpse.data.repository.MainRepository
import com.example.geoglimpse.ui.viewmodel.SelectCountryViewModel
import java.lang.IllegalArgumentException

class SelectCountryViewModelFactory constructor(
    private val repository: MainRepository,
    private val historyRepository: HistoryRepositoryInterface
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SelectCountryViewModel::class.java)) {
            SelectCountryViewModel(this.repository, this.historyRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}