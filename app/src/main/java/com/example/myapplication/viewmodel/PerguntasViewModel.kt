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

        viewModelScope.launch {
            perguntaRepository.listarTodasFlow().collect { lista ->
                if (lista == null) {
                    _uiState.update { it.copy(erro = "Não foi possível carregar as perguntas") }
                } else {
                    _uiState.update { state ->
                        state.copy(perguntas = lista.sortedBy { it.enunciado.lowercase() })
                    }
                }
            }
        }

        onSincronizar()
    }

    fun onSincronizar() {
        _uiState.update { it.copy(sincronizando = true) }
        viewModelScope.launch {
            val sincronizou = perguntaRepository.sincronizar()
            _uiState.update {
                if (sincronizou) {
                    it.copy(sincronizando = false, erro = "")
                } else {
                    it.copy(
                        sincronizando = false,
                        erro = "Sem conexão com o Firebase. Mostrando as perguntas salvas no aparelho.",
                        sucesso = ""
                    )
                }
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

    fun onEditar(pergunta: Pergunta) {
        _uiState.update {
            it.copy(
                editando = pergunta,
                enunciado = pergunta.enunciado,
                alternativas = listOf(
                    pergunta.alternativaA,
                    pergunta.alternativaB,
                    pergunta.alternativaC,
                    pergunta.alternativaD
                ),
                indiceCorreta = pergunta.indiceCorreta,
                erro = "",
                sucesso = ""
            )
        }
    }

    fun onCancelarEdicao() {
        _uiState.update { limparFormulario(it).copy(erro = "", sucesso = "") }
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

        val editando = state.editando
        val pergunta = Pergunta(
            id = editando?.id ?: 0,
            idRemoto = editando?.idRemoto ?: "",
            enunciado = enunciado,
            alternativaA = alternativas[0],
            alternativaB = alternativas[1],
            alternativaC = alternativas[2],
            alternativaD = alternativas[3],
            indiceCorreta = indiceCorreta
        )

        viewModelScope.launch {
            val salvou = if (editando == null) {
                perguntaRepository.inserir(pergunta)
            } else {
                perguntaRepository.atualizar(pergunta)
            }
            if (salvou) {
                val mensagem = if (editando == null) "Pergunta cadastrada!" else "Pergunta atualizada!"
                _uiState.update { limparFormulario(it).copy(erro = "", sucesso = mensagem) }
            } else {
                _uiState.update {
                    it.copy(erro = "Não foi possível salvar no Firebase. Verifique a internet.", sucesso = "")
                }
            }
        }
    }

    fun onExcluir(pergunta: Pergunta) {
        viewModelScope.launch {
            val excluiu = perguntaRepository.excluir(pergunta)
            _uiState.update {
                if (excluiu) {
                    val estado = if (it.editando?.id == pergunta.id) limparFormulario(it) else it
                    estado.copy(erro = "", sucesso = "Pergunta excluída!")
                } else {
                    it.copy(erro = "Não foi possível excluir no Firebase. Verifique a internet.", sucesso = "")
                }
            }
        }
    }

    private fun limparFormulario(state: PerguntasUiState) = state.copy(
        editando = null,
        enunciado = "",
        alternativas = List(4) { "" },
        indiceCorreta = null
    )

}
