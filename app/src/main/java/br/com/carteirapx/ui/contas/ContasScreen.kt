package br.com.carteirapx.ui.contas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import br.com.carteirapx.data.entity.Account
import br.com.carteirapx.data.entity.AccountType
import br.com.carteirapx.util.centavosToDisplay
import br.com.carteirapx.util.centavosToInputText
import br.com.carteirapx.util.parseToCentavos

@Composable
fun ContasScreen(vm: ContasViewModel = hiltViewModel()) {
    val accounts by vm.accounts.collectAsState()
    var editing by remember { mutableStateOf<Account?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showDialog = true }) {
                Icon(Icons.Default.Add, "Nova conta")
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            Text("Contas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${accounts.size} conta(s)", style = MaterialTheme.typography.labelSmall)

            if (accounts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhuma conta cadastrada.\nToque no + para adicionar a primeira.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accounts, key = { it.id }) { acc ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(acc.name, fontWeight = FontWeight.Medium)
                                    Text(acc.type.label(), style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        acc.initialBalance.centavosToDisplay(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Row {
                                    IconButton({ editing = acc; showDialog = true }) { Icon(Icons.Default.Edit, "Editar") }
                                    IconButton({ vm.delete(acc) }) { Icon(Icons.Default.Delete, "Remover") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AccountDialog(
            initial = editing,
            onDismiss = { showDialog = false },
            onSave = { vm.save(it); showDialog = false }
        )
    }
}

private fun AccountType.label(): String = when (this) {
    AccountType.CORRENTE -> "Conta corrente"
    AccountType.POUPANCA -> "Poupança"
    AccountType.CARTEIRA -> "Carteira"
    AccountType.INVESTIMENTO -> "Investimento"
    AccountType.OUTRA -> "Outra"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountDialog(initial: Account?, onDismiss: () -> Unit, onSave: (Account) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: AccountType.CORRENTE) }
    var balanceText by remember { mutableStateOf(initial?.initialBalance?.centavosToInputText() ?: "0,00") }
    var typeMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nova conta" else "Editar conta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = typeMenu, onExpandedChange = { typeMenu = it }) {
                    OutlinedTextField(
                        value = type.label(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenu) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    DropdownMenu(typeMenu, { typeMenu = false }) {
                        AccountType.entries.forEach { t ->
                            DropdownMenuItem(text = { Text(t.label()) }, onClick = { type = t; typeMenu = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Saldo inicial (R$)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(
                        (initial ?: Account(name = name, type = type, initialBalance = 0)).copy(
                            name = name.trim(),
                            type = type,
                            initialBalance = parseToCentavos(balanceText)
                        )
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } }
    )
}
