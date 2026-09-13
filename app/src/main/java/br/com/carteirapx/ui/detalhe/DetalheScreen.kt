package br.com.carteirapx.ui.detalhe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.ui.categorias.CategoryIcons
import br.com.carteirapx.ui.common.color
import br.com.carteirapx.ui.common.label
import br.com.carteirapx.ui.common.sign
import br.com.carteirapx.util.centavosToDisplay
import br.com.carteirapx.util.centavosToInputText
import br.com.carteirapx.util.parseToCentavos
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(transactionId: Long, onBack: () -> Unit, vm: DetalheViewModel = hiltViewModel()) {
    LaunchedEffect(transactionId) { vm.load(transactionId) }
    val t by vm.transaction.collectAsState()
    val categories by vm.categories.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe do lançamento") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } }
            )
        }
    ) { pad ->
        val current = t
        if (current == null) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        val categoria = categories.firstOrNull { it.id == current.categoryId }
        val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }
        val podeConfirmar = current.status in setOf(TransactionStatus.PREVISTA, TransactionStatus.VENCENDO, TransactionStatus.ATRASADA)
        val podeCancelar = current.status != TransactionStatus.CONFIRMADA && current.status != TransactionStatus.CANCELADA

        Column(Modifier.padding(pad).fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(CategoryIcons.iconFor(categoria?.iconName ?: "category"), null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(current.description, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(categoria?.name ?: "Sem categoria", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text(current.status.label(), color = current.status.color(), fontWeight = FontWeight.Bold)

            HorizontalDivider()

            InfoRow("Valor previsto", current.valorPrevisto.centavosToDisplay())
            InfoRow("Data prevista", dateFmt.format(current.dataPrevista))
            current.valorRealizado?.let { InfoRow("Valor realizado", it.centavosToDisplay()) }
            current.dataRealizada?.let { InfoRow("Data realizada", dateFmt.format(it)) }
            if (current.notes.isNotBlank()) InfoRow("Notas", current.notes)

            HorizontalDivider()

            if (podeConfirmar) {
                Button(onClick = { showConfirmDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Confirmar")
                }
            }
            if (podeCancelar) {
                OutlinedButton(onClick = vm::cancelar, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar lançamento")
                }
            }
            TextButton(onClick = { vm.excluir(onBack) }, modifier = Modifier.fillMaxWidth()) {
                Text("Excluir", color = MaterialTheme.colorScheme.error)
            }
        }

        if (showConfirmDialog) {
            ConfirmDialog(
                valorSugerido = current.valorPrevisto,
                onDismiss = { showConfirmDialog = false },
                onConfirm = { valor ->
                    vm.confirmar(valor)
                    showConfirmDialog = false
                }
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ConfirmDialog(valorSugerido: Long, onDismiss: () -> Unit, onConfirm: (Long) -> Unit) {
    var valorText by remember { mutableStateOf(valorSugerido.centavosToInputText()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar lançamento") },
        text = {
            Column {
                Text("Confirme o valor real (pode ser diferente do previsto):", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = valorText,
                    onValueChange = { valorText = it },
                    label = { Text("Valor realizado (R$)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(parseToCentavos(valorText)) }) { Text("Confirmar") } },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } }
    )
}
