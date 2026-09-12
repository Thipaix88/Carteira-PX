package br.com.carteirapx.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(vm: DashboardViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsState()
    val fmt = remember(state) { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Card(Modifier.fillMaxSize().padding(top = 4.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Saldo real (confirmado)", style = MaterialTheme.typography.labelLarge)
                    Text(
                        fmt.format(state.saldoReal / 100.0),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Projeção para os próximos 30 dias", style = MaterialTheme.typography.labelLarge)
                    Text(
                        fmt.format(state.saldoProjetado30d / 100.0),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    "Sem contas ou lançamentos ainda — cadastre uma conta para começar.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
