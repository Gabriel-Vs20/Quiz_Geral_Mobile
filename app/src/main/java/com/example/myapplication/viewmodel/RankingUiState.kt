package com.example.myapplication.viewmodel

import com.example.myapplication.data.local.ResultadoFinal

data class ItemRanking(
    val resultado: ResultadoFinal,
    val porcentagem: Int
)

data class RankingUiState(
    val carregado: Boolean = false,
    val itens: List<ItemRanking> = emptyList(),
    val busca: String = "",
    val editando: ResultadoFinal? = null,
    val nomeEditado: String = "",
    val erro: String = "",
    val sucesso: String = ""
)
