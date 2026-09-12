package br.com.carteirapx.ui.lancar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Placeholder — Fase 1: fluxo único (tipo -> descrição -> valor -> categoria -> data -> conta -> recorrência opcional).
 * Aberto via FAB como bottom sheet, conforme combinado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LancarBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Text(
            "Lançar (em construção)\n\nFluxo: tipo → descrição → valor → categoria → data → conta → recorrência opcional",
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
