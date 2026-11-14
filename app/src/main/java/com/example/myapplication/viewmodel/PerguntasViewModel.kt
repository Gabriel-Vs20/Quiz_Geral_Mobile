package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.Pergunta
import com.example.myapplication.data.repository.PerguntaRepository
import com.example.myapplication.logic.RegrasQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerguntasViewModel(
    private val perguntaRepository: PerguntaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerguntasUiState())
    val uiState: StateFlow<PerguntasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            perguntaRepository.contar().collect { quantidade ->
                _uiState.update { it.copy(quantidadeCadastrada = quantidade) }
            }
        }
    }

    fun onEnunciadoChange(valor: String) {
        _uiState.update { it.copy(enunciado = valor, sucesso = "") }
    }

    fun onAlternativaChange(indice: Int, valor: String) {
        _uiState.update { state ->
            state.copy(
                alternativas = state.alternativas.mapIndexed { i, atual ->
                    if (i == indice) valor else atual
                },
                sucesso = ""
            )
        }
    }

    fun onCorretaChange(indice: Int) {
        _uiState.update { it.copy(indiceCorreta = indice, sucesso = "") }
    }

    fun onSalvar() {
        val state = _uiState.value
        val enunciado = state.enunciado.trim()
        val alternativas = state.alternativas.map { it.trim() }
        val indiceCorreta = state.indiceCorreta

        val erro = RegrasQuiz.validarPergunta(enunciado, alternativas, indiceCorreta)
        if (erro.isNotBlank() || indiceCorreta == null) {
            _uiState.update { it.copy(erro = erro, sucesso = "") }
            return
        }

        val pergunta = Pergunta(
            enunciado = enunciado,
            alternativaA = alternativas[0],
            alternativaB = alternativas[1],
            alternativaC = alternativas[2],
            alternativaD = alternativas[3],
            indiceCorreta = indiceCorreta
        )

        viewModelScope.launch {
            val salvou = perguntaRepository.inserir(pergunta)
            if (salvou) {
                _uiState.update {
                    it.copy(
                        enunciado = "",
                        alternativas = List(4) { "" },
                        indiceCorreta = null,
                        erro = "",
                        sucesso = "Pergunta cadastrada!"
                    )
                }
            } else {
                _uiState.update { it.copy(erro = "Não foi possível salvar a pergunta", sucesso = "") }
            }
        }
    }

}
