package br.com.carteirapx.ui.resumo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.carteirapx.util.centavosToDisplay

@Composable
fun ResumoScreen(vm: ResumoViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Resumo mensal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Gastos por categoria (mês atual)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(state.totalMes.centavosToDisplay(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)

                if (state.fatias.isEmpty()) {
                    Text(
                        "Sem despesas confirmadas ou previstas este mês ainda.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                } else {
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        PieChart(state.fatias, state.totalMes, Modifier.size(140.dp))
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            state.fatias.forEach { fatia ->
                                val pct = if (state.totalMes > 0) (fatia.valor * 100 / state.totalMes) else 0
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(fatia.color))
                                    Spacer(Modifier.width(6.dp))
                                    Text("${fatia.name} • $pct%", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Evolução mensal (despesas)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                BarChart(state.meses, Modifier.fillMaxWidth().height(140.dp).padding(top = 12.dp))
            }
        }
    }
}

@Composable
private fun PieChart(fatias: List<CategorySlice>, total: Long, modifier: Modifier = Modifier) {
    Canvas(modifier.aspectRatio(1f)) {
        var startAngle = -90f
        fatias.forEach { fatia ->
            val sweep = if (total > 0) (fatia.valor.toFloat() / total.toFloat()) * 360f else 0f
            drawArc(color = fatia.color, startAngle = startAngle, sweepAngle = sweep, useCenter = true)
            startAngle += sweep
        }
    }
}

@Composable
private fun BarChart(meses: List<MonthBar>, modifier: Modifier = Modifier) {
    val max = (meses.maxOfOrNull { it.valor } ?: 0L).coerceAtLeast(1L)
    Row(modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        meses.forEach { mes ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize().weight(1f)) {
                val fracao = mes.valor.toFloat() / max.toFloat()
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1f, fill = true),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(fracao.coerceIn(0.02f, 1f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                Text(mes.label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
