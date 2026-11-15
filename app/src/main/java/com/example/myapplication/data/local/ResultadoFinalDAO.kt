package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultadoFinalDAO {

    @Insert
    suspend fun inserir(resultado: ResultadoFinal)

    @Query("SELECT * FROM resultado_final")
    fun listarTodos(): Flow<List<ResultadoFinal>>

    @Update
    suspend fun atualizar(resultado: ResultadoFinal)

    @Delete
    suspend fun excluir(resultado: ResultadoFinal)

}
