package com.example.myapplication.data.repository

import com.example.myapplication.data.local.Pergunta
import com.example.myapplication.data.local.PerguntaDAO
import kotlinx.coroutines.flow.Flow

class PerguntaRepository(private val perguntaDao: PerguntaDAO) {

    fun contar(): Flow<Int> = perguntaDao.contar()

    suspend fun listarTodas(): List<Pergunta>? {
        return try {
            perguntaDao.listarTodas()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun inserir(pergunta: Pergunta): Boolean {
        return try {
            perguntaDao.inserir(pergunta)
            true
        } catch (e: Exception) {
            false
        }
    }

}
