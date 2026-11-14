package com.example.myapplication.viewmodel

import com.example.myapplication.data.local.Pergunta

data class QuizClienteUiState(
    val carregado: Boolean = false,
    val perguntas: List<Pergunta> = emptyList(),
    val indiceAtual: Int = 0,
    val respostaEscolhida: Int? = null,
    val acertos: Int = 0,
    val finalizado: Boolean = false,
    val erro: String = ""
) {
    val perguntaAtual: Pergunta? get() = perguntas.getOrNull(indiceAtual)
}
