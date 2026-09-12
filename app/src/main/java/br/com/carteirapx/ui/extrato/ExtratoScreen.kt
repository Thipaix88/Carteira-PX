package br.com.carteirapx.ui.extrato

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Placeholder — Fase 1: lista filtrável de lançamentos (previstos + confirmados). */
@Composable
fun ExtratoScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Extrato (em construção)", style = MaterialTheme.typography.bodyLarge)
    }
}
