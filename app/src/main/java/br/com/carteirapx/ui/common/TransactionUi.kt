package br.com.carteirapx.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.entity.TransactionType

/** Lançamento já resolvido com o nome/ícone da categoria, pronto para exibir na UI. */
data class TransactionUi(
    val transaction: Transaction,
    val categoryName: String,
    val categoryIcon: String
)

fun TransactionStatus.label(): String = when (this) {
    TransactionStatus.PREVISTA -> "Previsto"
    TransactionStatus.VENCENDO -> "Vence hoje"
    TransactionStatus.ATRASADA -> "Atrasado"
    TransactionStatus.CONFIRMADA -> "Confirmado"
    TransactionStatus.CANCELADA -> "Cancelado"
}

@Composable
fun TransactionStatus.color(): Color = when (this) {
    TransactionStatus.PREVISTA -> MaterialTheme.colorScheme.onSurfaceVariant
    TransactionStatus.VENCENDO -> Color(0xFFFFA000)
    TransactionStatus.ATRASADA -> MaterialTheme.colorScheme.error
    TransactionStatus.CONFIRMADA -> MaterialTheme.colorScheme.primary
    TransactionStatus.CANCELADA -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
fun TransactionType.color(): Color = when (this) {
    TransactionType.RECEITA -> MaterialTheme.colorScheme.primary
    TransactionType.DESPESA -> MaterialTheme.colorScheme.error
}

fun TransactionType.sign(): String = if (this == TransactionType.RECEITA) "+ " else "- "
