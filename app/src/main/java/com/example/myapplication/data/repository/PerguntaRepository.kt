package com.example.myapplication.data.repository

import com.example.myapplication.data.local.Pergunta
import com.example.myapplication.data.local.PerguntaDAO
import com.example.myapplication.data.remote.PerguntaRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PerguntaRepository(
    private val perguntaDao: PerguntaDAO,
    private val perguntaRemota: PerguntaRemoteDataSource
) {

    fun contar(): Flow<Int> = perguntaDao.contar()

    fun listarTodasFlow(): Flow<List<Pergunta>?> {
        return perguntaDao.listarTodasFlow()
            .map<List<Pergunta>, List<Pergunta>?> { it }
            .catch { emit(null) }
    }

    suspend fun listarTodas(): List<Pergunta>? {
        return try {
            perguntaDao.listarTodas()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sincronizar(): Boolean {
        return try {
            val perguntas = perguntaRemota.listar()
            perguntaDao.substituirTodas(perguntas)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun inserir(pergunta: Pergunta): Boolean {
        return try {
            val idRemoto = perguntaRemota.inserir(pergunta)
            perguntaDao.inserir(pergunta.copy(idRemoto = idRemoto))
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun atualizar(pergunta: Pergunta): Boolean {
        return try {
            if (pergunta.idRemoto.isBlank()) {
                val idRemoto = perguntaRemota.inserir(pergunta)
                perguntaDao.atualizar(pergunta.copy(idRemoto = idRemoto))
            } else {
                perguntaRemota.atualizar(pergunta)
                perguntaDao.atualizar(pergunta)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun excluir(pergunta: Pergunta): Boolean {
        return try {
            if (pergunta.idRemoto.isNotBlank()) {
                perguntaRemota.excluir(pergunta.idRemoto)
            }
            perguntaDao.excluir(pergunta)
            true
        } catch (e: Exception) {
            false
        }
    }

}
