package com.example.myapplication.data.remote

import com.example.myapplication.data.local.Pergunta
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

// Acesso à coleção "perguntas" do Firebase Firestore.
// As funções podem lançar exceção (sem internet, tempo esgotado, permissão negada);
// quem trata é o PerguntaRepository, com try-catch.
class PerguntaRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val colecao = firestore.collection("perguntas")

    suspend fun listar(): List<Pergunta> {
        // Source.SERVER obriga a buscar na nuvem (sem internet -> exceção)
        val resultado = withTimeout(TEMPO_LIMITE) { colecao.get(Source.SERVER).await() }
        return resultado.documents.map { paraPergunta(it) }
    }

    // Retorna o ID gerado pelo Firestore para o novo documento
    suspend fun inserir(pergunta: Pergunta): String {
        val documento = withTimeout(TEMPO_LIMITE) { colecao.add(paraMapa(pergunta)).await() }
        return documento.id
    }

    suspend fun atualizar(pergunta: Pergunta) {
        withTimeout(TEMPO_LIMITE) { colecao.document(pergunta.idRemoto).set(paraMapa(pergunta)).await() }
    }

    suspend fun excluir(idRemoto: String) {
        withTimeout(TEMPO_LIMITE) { colecao.document(idRemoto).delete().await() }
    }

    private fun paraMapa(pergunta: Pergunta): Map<String, Any> = mapOf(
        "enunciado" to pergunta.enunciado,
        "alternativaA" to pergunta.alternativaA,
        "alternativaB" to pergunta.alternativaB,
        "alternativaC" to pergunta.alternativaC,
        "alternativaD" to pergunta.alternativaD,
        "indiceCorreta" to pergunta.indiceCorreta
    )

    private fun paraPergunta(documento: DocumentSnapshot) = Pergunta(
        idRemoto = documento.id,
        enunciado = documento.getString("enunciado") ?: "",
        alternativaA = documento.getString("alternativaA") ?: "",
        alternativaB = documento.getString("alternativaB") ?: "",
        alternativaC = documento.getString("alternativaC") ?: "",
        alternativaD = documento.getString("alternativaD") ?: "",
        indiceCorreta = documento.getLong("indiceCorreta")?.toInt() ?: 0
    )

    private companion object {
        const val TEMPO_LIMITE = 10_000L
    }

}
