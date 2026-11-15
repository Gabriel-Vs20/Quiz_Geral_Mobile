package com.example.myapplication.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private data class AbaJogador(val rota: String, val titulo: String, val icone: ImageVector)

private val abasJogador = listOf(
    AbaJogador(Rotas.QUIZ_CLIENTE, "Jogar", Icons.Default.PlayArrow),
    AbaJogador(Rotas.RANKING, "Ranking", Icons.Default.Star),
    AbaJogador(Rotas.SOBRE, "Sobre", Icons.Default.Info)
)

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    val mostrarBarra = abasJogador.any { it.rota == rotaAtual }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                BarraInferiorJogador(navController, rotaAtual)
            }
        }
    ) { espacoInterno ->

        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN_ADM,
            modifier = Modifier
                .padding(espacoInterno)
                .consumeWindowInsets(espacoInterno)
        ) {

            composable(Rotas.LOGIN_ADM) {
                TelaLoginAdm(navController)
            }

            composable(Rotas.PERGUNTAS_ADM) {
                TelaPerguntasAdm(navController)
            }

            composable(Rotas.QUIZ_CLIENTE) {
                TelaQuizCliente(navController)
            }

            composable(
                Rotas.RESULTADO_CLIENTE,
                arguments = listOf(
                    navArgument("acertos") { type = NavType.IntType },
                    navArgument("total") { type = NavType.IntType }
                )
            ) { entrada ->
                TelaResultadoCliente(
                    navController,
                    acertos = entrada.arguments?.getInt("acertos") ?: 0,
                    total = entrada.arguments?.getInt("total") ?: 0
                )
            }

            composable(Rotas.RANKING) {
                TelaRanking(navController)
            }

            composable(Rotas.SOBRE) {
                TelaSobre()
            }

        }
    }

}

@Composable
private fun BarraInferiorJogador(navController: NavHostController, rotaAtual: String?) {
    NavigationBar(containerColor = Color.White) {
        abasJogador.forEach { aba ->
            NavigationBarItem(
                selected = rotaAtual == aba.rota,
                onClick = {
                    if (rotaAtual != aba.rota) {
                        navController.navigate(aba.rota) {
                            popUpTo(rotaAtual ?: aba.rota) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                label = { Text(aba.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = CorCliente,
                    indicatorColor = CorCliente
                )
            )
        }
    }
}
