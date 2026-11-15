package com.example.myapplication.viewmodel

import com.example.myapplication.data.local.Pergunta

data class PerguntasUiState(
    val quantidadeCadastrada: Int = 0,
    val perguntas: List<Pergunta> = emptyList(),
    val sincronizando: Boolean = false,
    val editando: Pergunta? = null,
    val enunciado: String = "",
    val alternativas: List<String> = List(4) { "" },
    val indiceCorreta: Int? = null,
    val erro: String = "",
    val sucesso: String = ""
)
