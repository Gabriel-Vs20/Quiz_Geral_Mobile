package com.example.myapplication.data.repository

import com.example.myapplication.data.local.ResultadoFinal
import com.example.myapplication.data.local.ResultadoFinalDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ResultadoFinalRepository(private val resultadoFinalDao: ResultadoFinalDAO) {

    fun listarTodos(): Flow<List<ResultadoFinal>?> {
        return resultadoFinalDao.listarTodos()
            .map<List<ResultadoFinal>, List<ResultadoFinal>?> { it }
            .catch { emit(null) }
    }

    suspend fun inserir(resultado: ResultadoFinal): Boolean {
        return try {
            resultadoFinalDao.inserir(resultado)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun atualizar(resultado: ResultadoFinal): Boolean {
        return try {
            resultadoFinalDao.atualizar(resultado)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun excluir(resultado: ResultadoFinal): Boolean {
        return try {
            resultadoFinalDao.excluir(resultado)
            true
        } catch (e: Exception) {
            false
        }
    }

}
