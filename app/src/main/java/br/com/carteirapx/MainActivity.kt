package br.com.carteirapx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.carteirapx.ui.categorias.CategoriasScreen
import br.com.carteirapx.ui.contas.ContasScreen
import br.com.carteirapx.ui.dashboard.DashboardScreen
import br.com.carteirapx.ui.detalhe.DetalheScreen
import br.com.carteirapx.ui.extrato.ExtratoScreen
import br.com.carteirapx.ui.lancar.LancarBottomSheet
import br.com.carteirapx.ui.navigation.Routes
import br.com.carteirapx.ui.resumo.ResumoScreen
import br.com.carteirapx.ui.theme.CarteiraPXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarteiraPXTheme { AppRoot() }
        }
    }
}

@Composable
private fun AppRoot() {
    val navController = rememberNavController()
    var showLancar by remember { mutableStateOf(false) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    // Contas e Categorias têm seu próprio FAB ("Nova conta"/"Nova categoria") — o FAB global
    // de Lançar fica escondido nessas telas para não sobrepor o botão certo.
    val showGlobalFab = currentRoute != Routes.Contas.route && currentRoute != Routes.Categorias.route

    Scaffold(
        bottomBar = { AppBottomBar(navController) },
        floatingActionButton = {
            if (showGlobalFab) {
                FloatingActionButton(onClick = { showLancar = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Lançar")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.Dashboard.route) { DashboardScreen() }
            composable(Routes.Extrato.route) { ExtratoScreen() }
            composable(Routes.Resumo.route) { ResumoScreen() }
            composable(Routes.Contas.route) { ContasScreen() }
            composable(Routes.Categorias.route) { CategoriasScreen() }
            composable(Routes.Detalhe.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull() ?: 0L
                DetalheScreen(id)
            }
        }
    }

    if (showLancar) {
        LancarBottomSheet(onDismiss = { showLancar = false })
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Routes.Dashboard.route,
            onClick = { navController.navigateSingleTopTo(Routes.Dashboard.route) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Extrato.route,
            onClick = { navController.navigateSingleTopTo(Routes.Extrato.route) },
            icon = { Icon(Icons.Default.List, null) },
            label = { Text("Extrato") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Resumo.route,
            onClick = { navController.navigateSingleTopTo(Routes.Resumo.route) },
            icon = { Icon(Icons.Default.PieChart, null) },
            label = { Text("Resumo") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Categorias.route,
            onClick = { navController.navigateSingleTopTo(Routes.Categorias.route) },
            icon = { Icon(Icons.Default.Category, null) },
            label = { Text("Categorias") }
        )
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) = navigate(route) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
}
