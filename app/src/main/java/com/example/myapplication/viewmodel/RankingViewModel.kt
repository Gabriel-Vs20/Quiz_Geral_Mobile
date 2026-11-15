package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.ResultadoFinal
import com.example.myapplication.data.repository.ResultadoFinalRepository
import com.example.myapplication.logic.RegrasQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingViewModel(
    private val resultadoFinalRepository: ResultadoFinalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    private var todos: List<ResultadoFinal> = emptyList()

    init {
        viewModelScope.launch {
            resultadoFinalRepository.listarTodos().collect { lista ->
                if (lista == null) {
                    _uiState.update {
                        it.copy(carregado = true, erro = "Não foi possível carregar o ranking")
                    }
                } else {
                    todos = lista
                    montarRanking()
                }
            }
        }
    }

    private fun montarRanking() {
        val busca = _uiState.value.busca.trim()
        val itens = todos
            .filter { busca.isBlank() || it.nomeJogador.contains(busca, ignoreCase = true) }
            .map { ItemRanking(it, RegrasQuiz.calcularPorcentagem(it.acertos, it.totalPerguntas)) }
            .sortedByDescending { it.resultado.dataHora }
            .sortedByDescending { it.porcentagem }
        _uiState.update { it.copy(carregado = true, itens = itens) }
    }

    fun onBuscaChange(valor: String) {
        _uiState.update { it.copy(busca = valor) }
        montarRanking()
    }

    fun onEditar(resultado: ResultadoFinal) {
        _uiState.update {
            it.copy(editando = resultado, nomeEditado = resultado.nomeJogador, erro = "", sucesso = "")
        }
    }

    fun onNomeEditadoChange(valor: String) {
        _uiState.update { it.copy(nomeEditado = valor, erro = "") }
    }

    fun onCancelarEdicao() {
        _uiState.update { it.copy(editando = null, nomeEditado = "", erro = "") }
    }

    fun onSalvarEdicao() {
        val state = _uiState.value
        val resultado = state.editando ?: return
        val nome = state.nomeEditado.trim()

        val erro = RegrasQuiz.validarNomeJogador(nome)
        if (erro.isNotBlank()) {
            _uiState.update { it.copy(erro = erro) }
            return
        }

        viewModelScope.launch {
            val salvou = resultadoFinalRepository.atualizar(resultado.copy(nomeJogador = nome))
            if (salvou) {
                _uiState.update {
                    it.copy(editando = null, nomeEditado = "", erro = "", sucesso = "Nome atualizado!")
                }
            } else {
                _uiState.update { it.copy(erro = "Não foi possível atualizar o resultado") }
            }
        }
    }

    fun onExcluir(resultado: ResultadoFinal) {
        viewModelScope.launch {
            val excluiu = resultadoFinalRepository.excluir(resultado)
            _uiState.update {
                if (excluiu) {
                    it.copy(erro = "", sucesso = "Resultado excluído!")
                } else {
                    it.copy(erro = "Não foi possível excluir o resultado", sucesso = "")
                }
            }
        }
    }

}
