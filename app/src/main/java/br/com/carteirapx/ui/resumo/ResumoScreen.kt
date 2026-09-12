package br.com.carteirapx.ui.resumo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Placeholder — Fase 1: gráfico de pizza por categoria + evolução mensal. */
@Composable
fun ResumoScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Resumo mensal (em construção)", style = MaterialTheme.typography.bodyLarge)
    }
}
