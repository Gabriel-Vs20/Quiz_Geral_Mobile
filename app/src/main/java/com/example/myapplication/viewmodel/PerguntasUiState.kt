package com.example.myapplication.viewmodel

data class PerguntasUiState(
    val quantidadeCadastrada: Int = 0,
    val enunciado: String = "",
    val alternativas: List<String> = List(4) { "" },
    val indiceCorreta: Int? = null,
    val erro: String = "",
    val sucesso: String = ""
)
