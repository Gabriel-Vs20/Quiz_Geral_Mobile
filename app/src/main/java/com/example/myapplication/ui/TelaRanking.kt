package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.ResultadoFinalRepository
import com.example.myapplication.viewmodel.ItemRanking
import com.example.myapplication.viewmodel.RankingViewModel
import com.example.myapplication.viewmodel.RankingViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelaRanking(
    navController: NavHostController,
    viewModel: RankingViewModel = viewModel(
        factory = RankingViewModelFactory(
            ResultadoFinalRepository(AppDatabase.getDatabase(LocalContext.current).resultadoFinalDAO())
        )
    )
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F7F6)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
        ) {

            Text("Ranking", color = CorCliente, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)

            Spacer(modifier = Modifier.padding(4.dp))

            CampoTexto(
                titulo = "BUSCAR JOGADOR",
                valor = uiState.busca,
                placeholder = "Digite um nome",
                aoAlterar = { viewModel.onBuscaChange(it) },
                corTema = CorCliente
            )

            if (uiState.erro.isNotBlank() && uiState.editando == null) {
                Spacer(modifier = Modifier.padding(4.dp))
                CaixaErro(uiState.erro)
            }
            if (uiState.sucesso.isNotBlank()) {
                Spacer(modifier = Modifier.padding(4.dp))
                CaixaSucesso(uiState.sucesso)
            }

            Spacer(modifier = Modifier.padding(6.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.carregado && uiState.itens.isEmpty()) {
                    item {
                        Text(
                            "Nenhum resultado encontrado",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7A74),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                itemsIndexed(uiState.itens, key = { _, item -> item.resultado.id }) { posicao, item ->
                    CartaoRanking(
                        posicao = posicao + 1,
                        item = item,
                        aoEditar = { viewModel.onEditar(item.resultado) },
                        aoExcluir = { viewModel.onExcluir(item.resultado) }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(6.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate(Rotas.LOGIN_ADM) {
                        popUpTo(Rotas.RANKING) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CorCliente),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Sair", fontWeight = FontWeight.Bold) }

        }
    }

    // Diálogo de edição (Update) aparece quando há um resultado sendo editado
    if (uiState.editando != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarEdicao() },
            title = { Text("Editar nome") },
            text = {
                Column {
                    CampoTexto(
                        titulo = "NOME DO JOGADOR",
                        valor = uiState.nomeEditado,
                        placeholder = "Digite o nome",
                        aoAlterar = { viewModel.onNomeEditadoChange(it) },
                        corTema = CorCliente
                    )
                    if (uiState.erro.isNotBlank()) {
                        Spacer(modifier = Modifier.padding(4.dp))
                        CaixaErro(uiState.erro)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onSalvarEdicao() }) {
                    Text("Salvar", color = CorCliente, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelarEdicao() }) {
                    Text("Cancelar", color = Color(0xFF6B7A74))
                }
            }
        )
    }

}

@Composable
private fun CartaoRanking(
    posicao: Int,
    item: ItemRanking,
    aoEditar: () -> Unit,
    aoExcluir: () -> Unit
) {
    val formatoData = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$posicao°",
            color = CorCliente,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.resultado.nomeJogador,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1F2937)
            )
            Text(
                "${item.resultado.acertos} de ${item.resultado.totalPerguntas} (${item.porcentagem}%)",
                fontSize = 13.sp,
                color = Color(0xFF1F2937)
            )
            Text(
                formatoData.format(Date(item.resultado.dataHora)),
                fontSize = 11.sp,
                color = Color(0xFF6B7A74)
            )
        }
        IconButton(onClick = aoEditar) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = CorCliente)
        }
        IconButton(onClick = aoExcluir) {
            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = CorAdm)
        }
    }
}
