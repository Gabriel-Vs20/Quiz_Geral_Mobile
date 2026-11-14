package com.example.myapplication.data.repository

import com.example.myapplication.data.local.ResultadoFinal
import com.example.myapplication.data.local.ResultadoFinalDAO

class ResultadoFinalRepository(private val resultadoFinalDao: ResultadoFinalDAO) {

    suspend fun inserir(resultado: ResultadoFinal): Boolean {
        return try {
            resultadoFinalDao.inserir(resultado)
            true
        } catch (e: Exception) {
            false
        }
    }

}
