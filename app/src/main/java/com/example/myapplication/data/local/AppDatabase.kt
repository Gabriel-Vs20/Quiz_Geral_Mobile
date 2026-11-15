package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Pergunta::class, ResultadoFinal::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun perguntaDAO(): PerguntaDAO

    abstract fun resultadoFinalDAO(): ResultadoFinalDAO

    companion object {

        private val MIGRACAO_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `resultado_final` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`acertos` INTEGER NOT NULL, " +
                        "`totalPerguntas` INTEGER NOT NULL, " +
                        "`dataHora` INTEGER NOT NULL)"
                )
            }
        }

        // Versão 3: resultado passa a ter o nome do jogador (editável no ranking)
        private val MIGRACAO_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `resultado_final` " +
                        "ADD COLUMN `nomeJogador` TEXT NOT NULL DEFAULT 'Jogador'"
                )
            }
        }

        // Versão 4: pergunta guarda o ID do documento no Firestore
        private val MIGRACAO_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `perguntas` ADD COLUMN `idRemoto` TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            val temInstance = INSTANCE

            if (temInstance != null) {
                return temInstance
            } else {

                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "quiz_database"
                    ).addMigrations(MIGRACAO_1_2, MIGRACAO_2_3, MIGRACAO_3_4).build()
                    INSTANCE = instance
                    return instance
                }

            }

        }

    }

}
