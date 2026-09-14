package br.com.carteirapx.ui.detalhe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.rememberDatePickerState
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
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.ui.categorias.CategoryIcons
import br.com.carteirapx.ui.common.color
import br.com.carteirapx.ui.common.label
import br.com.carteirapx.util.centavosToDisplay
import br.com.carteirapx.util.centavosToInputText
import br.com.carteirapx.util.localDateToUtcMillis
import br.com.carteirapx.util.parseToCentavos
import br.com.carteirapx.util.utcMillisToLocalDate
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(transactionId: Long, onBack: () -> Unit, vm: DetalheViewModel = hiltViewModel()) {
    LaunchedEffect(transactionId) { vm.load(transactionId) }
    val t by vm.transaction.collectAsState()
    val categories by vm.categories.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

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

            OutlinedButton(onClick = { showEditDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Editar")
            }
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

        if (showEditDialog) {
            EditDialog(
                description = current.description,
                valorPrevisto = current.valorPrevisto,
                categoryId = current.categoryId,
                dataPrevista = current.dataPrevista,
                categorias = categories.filter { it.type == current.type },
                onDismiss = { showEditDialog = false },
                onSave = { desc, valor, catId, data ->
                    vm.editar(desc, valor, catId, data)
                    showEditDialog = false
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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun EditDialog(
    description: String,
    valorPrevisto: Long,
    categoryId: Long,
    dataPrevista: Long,
    categorias: List<Category>,
    onDismiss: () -> Unit,
    onSave: (String, Long, Long, Long) -> Unit
) {
    var desc by remember { mutableStateOf(description) }
    var valorText by remember { mutableStateOf(valorPrevisto.centavosToInputText()) }
    var catId by remember { mutableStateOf(categoryId) }
    var data by remember { mutableStateOf(dataPrevista) }
    var categoryMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }
    val categoriaSelecionada = categorias.firstOrNull { it.id == catId } ?: categorias.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar lançamento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrição") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = valorText, onValueChange = { valorText = it }, label = { Text("Valor (R$)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = categoryMenu, onExpandedChange = { categoryMenu = it }) {
                    OutlinedTextField(
                        value = categoriaSelecionada?.name ?: "Nenhuma",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenu) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    DropdownMenu(categoryMenu, { categoryMenu = false }) {
                        categorias.forEach { c ->
                            DropdownMenuItem(text = { Text(c.name) }, onClick = { catId = c.id; categoryMenu = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = dateFmt.format(data),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { TextButton(onClick = { showDatePicker = true }) { Text("Alterar") } }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = desc.isNotBlank() && categoriaSelecionada != null,
                onClick = { onSave(desc.trim(), parseToCentavos(valorText), categoriaSelecionada!!.id, data) }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } }
    )

    if (showDatePicker) {
        val initialUtc = remember(data) { localDateToUtcMillis(data) }
        val state = rememberDatePickerState(initialSelectedDateMillis = initialUtc)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { data = utcMillisToLocalDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton({ showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = state) }
    }
}
