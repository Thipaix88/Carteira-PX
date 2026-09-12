package br.com.carteirapx.ui.detalhe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Placeholder — Fase 1: detalhe do lançamento, com botão Confirmar. */
@Composable
fun DetalheScreen(transactionId: Long) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Detalhe do lançamento #$transactionId (em construção)", style = MaterialTheme.typography.bodyLarge)
    }
}
