package br.com.carteirapx.ui.categorias

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Placeholder — Fase 1: CRUD de categorias com ícone. */
@Composable
fun CategoriasScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Categorias (em construção)", style = MaterialTheme.typography.bodyLarge)
    }
}
