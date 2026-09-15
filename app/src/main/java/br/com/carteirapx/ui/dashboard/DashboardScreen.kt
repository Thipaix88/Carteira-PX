package br.com.carteirapx.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.carteirapx.data.entity.TransactionType
import br.com.carteirapx.domain.usecase.PeriodDeficitAlert
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
    var saldoExpandido by remember { mutableStateOf(false) }
    var listaAberta by remember { mutableStateOf<TransactionType?>(null) }

    val dateFmtCurto = remember { SimpleDateFormat("dd/MM", Locale("pt", "BR")) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Gaveta discreta — saldo acumulado desde sempre + projeção, fica fechada por padrão
        Card(Modifier.fillMaxWidth().clickable { saldoExpandido = !saldoExpandido }) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Saldo acumulado e projeção", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Icon(if (saldoExpandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                }
                AnimatedVisibility(saldoExpandido) {
                    Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column {
                            Text("Você tem hoje (tudo já confirmado)", style = MaterialTheme.typography.labelLarge)
                            Text(state.saldoReal.centavosToDisplay(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Se tudo previsto acontecer, em 30 dias você deve ter", style = MaterialTheme.typography.labelLarge)
                            Text(state.saldoProjetado30d.centavosToDisplay(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = vm::mesAnterior) { Icon(Icons.Default.ChevronLeft, "Mês anterior") }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(state.mesLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        if (state.alertaPeriodo != null) {
                            Icon(Icons.Default.WarningAmber, "Alerta", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(onClick = vm::proximoMes) { Icon(Icons.Default.ChevronRight, "Próximo mês") }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.clickable { listaAberta = TransactionType.RECEITA }) {
                        Text("Receitas", style = MaterialTheme.typography.labelSmall)
                        Text(state.receitasMes.centavosToDisplay(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                    Column(Modifier.clickable { listaAberta = TransactionType.DESPESA }) {
                        Text("Despesas", style = MaterialTheme.typography.labelSmall)
                        Text(state.despesasMes.centavosToDisplay(), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                    }
                    Column {
                        Text("Saldo do mês", style = MaterialTheme.typography.labelSmall)
                        Text(
                            state.saldoMes.centavosToDisplay(),
                            fontWeight = FontWeight.Bold,
                            color = if (state.saldoMes >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
                state.alertaPeriodo?.let { alerta ->
                    Row(
                        Modifier.fillMaxWidth().padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.WarningAmber, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Text(
                            "Alerta, você tem ${alerta.valorExcedente.centavosToDisplay()} a mais em despesa no período de " +
                                "${dateFmtCurto.format(alerta.inicio)} a ${dateFmtCurto.format(alerta.fim)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
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
            state.proximos.forEach { item ->
                CompromissoRow(item, dateFmtCurto, onClick = { onOpenDetail(item.transaction.id) })
            }
        }
    }

    listaAberta?.let { tipo ->
        val itens = state.transacoesMes.filter { it.transaction.type == tipo }
        AlertDialog(
            onDismissRequest = { listaAberta = null },
            title = { Text(if (tipo == TransactionType.RECEITA) "Receitas do mês" else "Despesas do mês") },
            text = {
                LazyColumn {
                    items(itens, key = { it.transaction.id }) { item ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { listaAberta = null; onOpenDetail(item.transaction.id) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.transaction.description, style = MaterialTheme.typography.bodyMedium)
                                Text(item.categoryName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(item.transaction.valorPrevisto.centavosToDisplay(), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { listaAberta = null }) { Text("Fechar") } }
        )
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
