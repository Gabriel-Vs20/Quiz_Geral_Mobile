package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PerguntaDAO {

    @Insert
    suspend fun inserir(pergunta: Pergunta)

    @Insert
    suspend fun inserirTodas(perguntas: List<Pergunta>)

    @Query("SELECT COUNT(*) FROM perguntas")
    fun contar(): Flow<Int>

    @Query("SELECT * FROM perguntas")
    suspend fun listarTodas(): List<Pergunta>

    @Query("SELECT * FROM perguntas")
    fun listarTodasFlow(): Flow<List<Pergunta>>

    @Update
    suspend fun atualizar(pergunta: Pergunta)

    @Delete
    suspend fun excluir(pergunta: Pergunta)

    @Query("DELETE FROM perguntas")
    suspend fun excluirTodas()

    @Transaction
    suspend fun substituirTodas(perguntas: List<Pergunta>) {
        excluirTodas()
        inserirTodas(perguntas)
    }

}
