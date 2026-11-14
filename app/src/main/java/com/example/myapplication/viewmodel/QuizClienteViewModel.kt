package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.ResultadoFinal
import com.example.myapplication.data.repository.PerguntaRepository
import com.example.myapplication.data.repository.ResultadoFinalRepository
import com.example.myapplication.logic.RegrasQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizClienteViewModel(
    private val perguntaRepository: PerguntaRepository,
    private val resultadoFinalRepository: ResultadoFinalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizClienteUiState())
    val uiState: StateFlow<QuizClienteUiState> = _uiState.asStateFlow()

    private var salvando = false

    init {
        viewModelScope.launch {
            val perguntas = perguntaRepository.listarTodas()
            val erro = when {
                perguntas == null -> "Não foi possível carregar as perguntas"
                perguntas.isEmpty() -> "Nenhuma pergunta cadastrada ainda"
                else -> ""
            }
            _uiState.update {
                it.copy(carregado = true, perguntas = perguntas ?: emptyList(), erro = erro)
            }
        }
    }

    fun onResponder(indice: Int) {
        val state = _uiState.value
        val pergunta = state.perguntaAtual ?: return
        if (state.respostaEscolhida != null) return
        _uiState.update {
            it.copy(
                respostaEscolhida = indice,
                acertos = if (RegrasQuiz.acertou(pergunta, indice)) it.acertos + 1 else it.acertos
            )
        }
    }

    fun onProxima() {
        val state = _uiState.value
        if (state.indiceAtual < state.perguntas.lastIndex) {
            _uiState.update { it.copy(indiceAtual = it.indiceAtual + 1, respostaEscolhida = null) }
            return
        }
        if (salvando) return
        salvando = true
        val resultado = ResultadoFinal(
            acertos = state.acertos,
            totalPerguntas = state.perguntas.size,
            dataHora = System.currentTimeMillis()
        )
        viewModelScope.launch {
            val salvou = resultadoFinalRepository.inserir(resultado)
            if (salvou) {
                _uiState.update { it.copy(finalizado = true, erro = "") }
            } else {
                salvando = false
                _uiState.update { it.copy(erro = "Não foi possível salvar o resultado") }
            }
        }
    }

}
