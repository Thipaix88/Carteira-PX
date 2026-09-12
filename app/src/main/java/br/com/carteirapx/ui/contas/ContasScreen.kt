package br.com.carteirapx.ui.contas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Placeholder — Fase 1: CRUD de contas. */
@Composable
fun ContasScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Contas (em construção)", style = MaterialTheme.typography.bodyLarge)
    }
}
