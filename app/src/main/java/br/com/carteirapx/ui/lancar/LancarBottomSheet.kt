package br.com.carteirapx.ui.lancar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.RecurrenceFrequency
import br.com.carteirapx.data.entity.TransactionType
import br.com.carteirapx.util.parseToCentavos
import java.text.SimpleDateFormat
import java.util.Locale

private fun RecurrenceFrequency.label(): String = when (this) {
    RecurrenceFrequency.SEMANAL -> "Semanal"
    RecurrenceFrequency.QUINZENAL -> "Quinzenal"
    RecurrenceFrequency.MENSAL -> "Mensal"
    RecurrenceFrequency.BIMESTRAL -> "Bimestral"
    RecurrenceFrequency.TRIMESTRAL -> "Trimestral"
    RecurrenceFrequency.SEMESTRAL -> "Semestral"
    RecurrenceFrequency.ANUAL -> "Anual"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LancarBottomSheet(onDismiss: () -> Unit, vm: LancarViewModel = hiltViewModel()) {
    val categories by vm.categories.collectAsState()

    var type by remember { mutableStateOf(TransactionType.DESPESA) }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<Long?>(null) }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var recorrente by remember { mutableStateOf(false) }
    var frequency by remember { mutableStateOf(RecurrenceFrequency.MENSAL) }

    var categoryMenu by remember { mutableStateOf(false) }
    var frequencyMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val categoriasDoTipo = remember(categories, type) { categories.filter { it.type == type } }
    val categoriaSelecionada = categoriasDoTipo.firstOrNull { it.id == categoryId } ?: categoriasDoTipo.firstOrNull()
    val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    val podeSalvar = description.isNotBlank() && parseToCentavos(amountText) > 0 && categoriaSelecionada != null

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Novo lançamento", style = MaterialTheme.typography.titleLarge)

            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = type == TransactionType.DESPESA,
                    onClick = { type = TransactionType.DESPESA; categoryId = null },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)
                ) { Text("Despesa") }
                SegmentedButton(
                    selected = type == TransactionType.RECEITA,
                    onClick = { type = TransactionType.RECEITA; categoryId = null },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)
                ) { Text("Receita") }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Valor (R$)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(expanded = categoryMenu, onExpandedChange = { categoryMenu = it }) {
                OutlinedTextField(
                    value = categoriaSelecionada?.name ?: "Nenhuma categoria disponível",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                DropdownMenu(categoryMenu, { categoryMenu = false }) {
                    categoriasDoTipo.forEach { c: Category ->
                        DropdownMenuItem(text = { Text(c.name) }, onClick = { categoryId = c.id; categoryMenu = false })
                    }
                }
            }

            OutlinedTextField(
                value = dateFmt.format(dateMillis),
                onValueChange = {},
                readOnly = true,
                label = { Text("Data") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { TextButton(onClick = { showDatePicker = true }) { Text("Alterar") } }
            )

            Column {
                androidx.compose.foundation.layout.Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Repetir (recorrência)")
                    Switch(checked = recorrente, onCheckedChange = { recorrente = it })
                }
                if (recorrente) {
                    ExposedDropdownMenuBox(expanded = frequencyMenu, onExpandedChange = { frequencyMenu = it }) {
                        OutlinedTextField(
                            value = frequency.label(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Frequência") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyMenu) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        DropdownMenu(frequencyMenu, { frequencyMenu = false }) {
                            RecurrenceFrequency.entries.forEach { f ->
                                DropdownMenuItem(text = { Text(f.label()) }, onClick = { frequency = f; frequencyMenu = false })
                            }
                        }
                    }
                }
            }

            Button(
                enabled = podeSalvar,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    vm.salvar(
                        type = type,
                        description = description.trim(),
                        amountCentavos = parseToCentavos(amountText),
                        categoryId = categoriaSelecionada!!.id,
                        dateMillis = dateMillis,
                        recurrence = if (recorrente) frequency else null,
                        onDone = onDismiss
                    )
                }
            ) { Text("Salvar") }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton({ showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = state) }
    }
}
