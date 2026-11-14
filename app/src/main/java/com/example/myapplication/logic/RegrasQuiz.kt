package com.example.myapplication.logic

import com.example.myapplication.data.local.Pergunta

object RegrasQuiz {

    fun validarPergunta(enunciado: String, alternativas: List<String>, indiceCorreta: Int?): String {
        return when {
            enunciado.isBlank() -> "Informe o enunciado da pergunta"
            alternativas.any { it.isBlank() } -> "Preencha as 4 alternativas"
            alternativas.distinctBy { it.lowercase() }.size < alternativas.size ->
                "As alternativas não podem ser iguais"
            indiceCorreta == null -> "Marque qual alternativa é a correta"
            else -> ""
        }
    }

    fun acertou(pergunta: Pergunta, indiceEscolhido: Int): Boolean {
        return indiceEscolhido == pergunta.indiceCorreta
    }

    fun calcularPorcentagem(acertos: Int, total: Int): Int {
        return if (total > 0) acertos * 100 / total else 0
    }

}
