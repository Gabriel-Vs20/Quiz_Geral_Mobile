package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.Pergunta
import com.example.myapplication.data.remote.PerguntaRemoteDataSource
import com.example.myapplication.data.repository.PerguntaRepository
import com.example.myapplication.viewmodel.PerguntasViewModel
import com.example.myapplication.viewmodel.PerguntasViewModelFactory

@Composable
fun TelaPerguntasAdm(
    navController: NavHostController,
    viewModel: PerguntasViewModel = viewModel(
        factory = PerguntasViewModelFactory(
            PerguntaRepository(
                AppDatabase.getDatabase(LocalContext.current).perguntaDAO(),
                PerguntaRemoteDataSource()
            )
        )
    )
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val coresDoCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = CorAdm,
        focusedLabelColor = CorAdm,
        cursorColor = CorAdm
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F7F6)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TituloSecao("Cadastro de perguntas")
                        Text(
                            if (uiState.sincronizando) "Sincronizando com o Firebase..."
                            else "${uiState.quantidadeCadastrada} cadastradas",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7A74)
                        )
                    }
                    TextButton(
                        onClick = { viewModel.onSincronizar() },
                        enabled = !uiState.sincronizando
                    ) {
                        Text("Atualizar", color = CorAdm, fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = {
                            navController.navigate(Rotas.LOGIN_ADM) {
                                popUpTo(Rotas.PERGUNTAS_ADM) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Sair", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        if (uiState.editando == null) "NOVA PERGUNTA" else "EDITANDO PERGUNTA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CorAdm
                    )
                    Spacer(modifier = Modifier.padding(4.dp))

                    OutlinedTextField(
                        value = uiState.enunciado,
                        onValueChange = { viewModel.onEnunciadoChange(it) },
                        label = { Text("Enunciado") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = coresDoCampo
                    )
                    Spacer(modifier = Modifier.padding(6.dp))

                    Text(
                        "Preencha as alternativas e marque a correta",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7A74)
                    )
                    Spacer(modifier = Modifier.padding(2.dp))

                    uiState.alternativas.forEachIndexed { indice, alternativa ->
                        val letra = 'A' + indice
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.indiceCorreta == indice,
                                onClick = { viewModel.onCorretaChange(indice) },
                                colors = RadioButtonDefaults.colors(selectedColor = CorAdm),
                                modifier = Modifier.semantics {
                                    contentDescription = "Marcar alternativa $letra como correta"
                                }
                            )
                            OutlinedTextField(
                                value = alternativa,
                                onValueChange = { viewModel.onAlternativaChange(indice, it) },
                                label = { Text("Alternativa $letra") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = coresDoCampo
                            )
                        }
                        Spacer(modifier = Modifier.padding(3.dp))
                    }

                    if (uiState.erro.isNotBlank()) {
                        Spacer(modifier = Modifier.padding(4.dp))
                        CaixaErro(uiState.erro)
                    }

                    if (uiState.sucesso.isNotBlank()) {
                        Spacer(modifier = Modifier.padding(4.dp))
                        CaixaSucesso(uiState.sucesso)
                    }

                    Spacer(modifier = Modifier.padding(6.dp))

                    Button(
                        onClick = { viewModel.onSalvar() },
                        colors = ButtonDefaults.buttonColors(containerColor = CorAdm),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            if (uiState.editando == null) "Salvar" else "Salvar alterações",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (uiState.editando != null) {
                        Spacer(modifier = Modifier.padding(3.dp))
                        OutlinedButton(
                            onClick = { viewModel.onCancelarEdicao() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CorAdm),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Cancelar edição", fontWeight = FontWeight.Bold) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.padding(4.dp))
                TituloSecao("Perguntas cadastradas")
            }

            if (uiState.perguntas.isEmpty()) {
                item {
                    Text(
                        "Nenhuma pergunta cadastrada ainda",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7A74)
                    )
                }
            }

            items(uiState.perguntas, key = { it.id }) { pergunta ->
                CartaoPergunta(
                    pergunta = pergunta,
                    aoEditar = { viewModel.onEditar(pergunta) },
                    aoExcluir = { viewModel.onExcluir(pergunta) }
                )
            }

        }
    }

}

@Composable
private fun CartaoPergunta(
    pergunta: Pergunta,
    aoEditar: () -> Unit,
    aoExcluir: () -> Unit
) {
    val alternativas = listOf(
        pergunta.alternativaA,
        pergunta.alternativaB,
        pergunta.alternativaC,
        pergunta.alternativaD
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                pergunta.enunciado,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1F2937)
            )
            Text(
                "Correta: ${'A' + pergunta.indiceCorreta}) ${alternativas.getOrElse(pergunta.indiceCorreta) { "" }}",
                fontSize = 13.sp,
                color = Color(0xFF00703C)
            )
        }
        IconButton(onClick = aoEditar) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = CorAdm)
        }
        IconButton(onClick = aoExcluir) {
            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = CorAdm)
        }
    }
}
