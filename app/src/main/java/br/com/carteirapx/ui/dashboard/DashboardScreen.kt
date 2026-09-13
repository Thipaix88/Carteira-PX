package br.com.carteirapx.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.carteirapx.ui.categorias.CategoryIcons
import br.com.carteirapx.ui.common.TransactionUi
import br.com.carteirapx.ui.common.color
import br.com.carteirapx.ui.common.label
import br.com.carteirapx.ui.common.sign
import br.com.carteirapx.util.centavosToDisplay
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DashboardScreen(onOpenDetail: (Long) -> Unit, vm: DashboardViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Saldo real (confirmado)", style = MaterialTheme.typography.labelLarge)
                    Text(
                        state.saldoReal.centavosToDisplay(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Projeção para os próximos 30 dias", style = MaterialTheme.typography.labelLarge)
                    Text(
                        state.saldoProjetado30d.centavosToDisplay(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Text("Próximos compromissos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        if (state.proximos.isEmpty()) {
            Text(
                "Nada por aqui ainda — toque no + para lançar algo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val dateFmt = remember { SimpleDateFormat("dd/MM", Locale("pt", "BR")) }
            state.proximos.forEach { item ->
                CompromissoRow(item, dateFmt, onClick = { onOpenDetail(item.transaction.id) })
            }
        }
    }
}

@Composable
private fun CompromissoRow(item: TransactionUi, dateFmt: SimpleDateFormat, onClick: () -> Unit) {
    val t = item.transaction
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(CategoryIcons.iconFor(item.categoryIcon), null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(t.description, fontWeight = FontWeight.Medium)
                    Text(
                        "${dateFmt.format(t.dataPrevista)} • ${t.status.label()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = t.status.color()
                    )
                }
            }
            Text("${t.type.sign()}${t.valorPrevisto.centavosToDisplay()}", fontWeight = FontWeight.Bold, color = t.type.color())
        }
    }
}
