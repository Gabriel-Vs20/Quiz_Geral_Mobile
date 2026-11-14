package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.ResultadoFinalRepository

class RankingViewModelFactory(
    private val resultadoFinalRepository: ResultadoFinalRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RankingViewModel(resultadoFinalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
