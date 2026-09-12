package br.com.carteirapx.ui.categorias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.TransactionType

@Composable
fun CategoriasScreen(vm: CategoriasViewModel = hiltViewModel()) {
    val categories by vm.categories.collectAsState()
    var editing by remember { mutableStateOf<Category?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf<TransactionType?>(null) }

    val visible = remember(categories, filterType) {
        if (filterType == null) categories else categories.filter { it.type == filterType }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showDialog = true }) {
                Icon(Icons.Default.Add, "Nova categoria")
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            Text("Categorias", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = filterType == null, onClick = { filterType = null }, label = { Text("Todas") })
                FilterChip(selected = filterType == TransactionType.RECEITA, onClick = { filterType = TransactionType.RECEITA }, label = { Text("Receitas") })
                FilterChip(selected = filterType == TransactionType.DESPESA, onClick = { filterType = TransactionType.DESPESA }, label = { Text("Despesas") })
            }

            if (visible.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma categoria aqui ainda.\nToque no + para adicionar.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(visible, key = { it.id }) { cat ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(CategoryIcons.iconFor(cat.iconName), null, tint = MaterialTheme.colorScheme.primary)
                                    Column {
                                        Text(cat.name, fontWeight = FontWeight.Medium)
                                        Text(
                                            if (cat.type == TransactionType.RECEITA) "Receita" else "Despesa",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                                Row {
                                    IconButton({ editing = cat; showDialog = true }) { Icon(Icons.Default.Edit, "Editar") }
                                    IconButton({ vm.delete(cat) }) { Icon(Icons.Default.Delete, "Remover") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CategoryDialog(
            initial = editing,
            onDismiss = { showDialog = false },
            onSave = { vm.save(it); showDialog = false }
        )
    }
}

@Composable
private fun CategoryDialog(initial: Category?, onDismiss: () -> Unit, onSave: (Category) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: TransactionType.DESPESA) }
    var icon by remember { mutableStateOf(initial?.iconName ?: "category") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nova categoria" else "Editar categoria") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = type == TransactionType.DESPESA,
                        onClick = { type = TransactionType.DESPESA },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) { Text("Despesa") }
                    SegmentedButton(
                        selected = type == TransactionType.RECEITA,
                        onClick = { type = TransactionType.RECEITA },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) { Text("Receita") }
                }

                Text("Ícone", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CategoryIcons.map.keys.toList()) { key ->
                        val selected = key == icon
                        IconButton(
                            onClick = { icon = key },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                CategoryIcons.iconFor(key),
                                null,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(
                        (initial ?: Category(name = name, type = type)).copy(
                            name = name.trim(),
                            type = type,
                            iconName = icon
                        )
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } }
    )
}
