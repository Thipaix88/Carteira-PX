package br.com.carteirapx.ui.extrato

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun ExtratoScreen(onOpenDetail: (Long) -> Unit, vm: ExtratoViewModel = hiltViewModel()) {
    val items by vm.items.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Extrato", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("${items.size} lançamento(s)", style = MaterialTheme.typography.labelSmall)

        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Nenhum lançamento ainda.\nToque no + para criar o primeiro.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.transaction.id }) { item ->
                    ExtratoItem(item, onClick = { onOpenDetail(item.transaction.id) })
                }
            }
        }
    }
}

@Composable
private fun ExtratoItem(item: TransactionUi, onClick: () -> Unit) {
    val t = item.transaction
    val dateFmt = remember(t.dataPrevista) { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(CategoryIcons.iconFor(item.categoryIcon), null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(t.description, fontWeight = FontWeight.Medium)
                    Text(
                        "${item.categoryName} • ${dateFmt.format(t.dataPrevista)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(t.status.label(), style = MaterialTheme.typography.labelSmall, color = t.status.color())
                }
            }
            Text(
                "${t.type.sign()}${t.valorPrevisto.centavosToDisplay()}",
                fontWeight = FontWeight.Bold,
                color = t.type.color()
            )
        }
    }
}
