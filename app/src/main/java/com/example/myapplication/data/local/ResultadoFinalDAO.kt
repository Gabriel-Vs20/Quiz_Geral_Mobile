package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultadoFinalDAO {

    // C - Create
    @Insert
    suspend fun inserir(resultado: ResultadoFinal)

    // R - Read (reativo: o Flow emite de novo sempre que a tabela muda)
    @Query("SELECT * FROM resultado_final")
    fun listarTodos(): Flow<List<ResultadoFinal>>

    // U - Update
    @Update
    suspend fun atualizar(resultado: ResultadoFinal)

    // D - Delete
    @Delete
    suspend fun excluir(resultado: ResultadoFinal)

}
