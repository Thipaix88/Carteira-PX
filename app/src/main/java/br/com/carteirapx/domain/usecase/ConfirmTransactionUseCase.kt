package br.com.carteirapx.domain.usecase

import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionStatus

/**
 * Confirmação só é permitida a partir de PREVISTA/VENCENDO/ATRASADA — nunca a partir de
 * CONFIRMADA ou CANCELADA. O valor realizado pode diferir do previsto (ex: conta veio com valor diferente).
 */
object ConfirmTransactionUseCase {
    operator fun invoke(transaction: Transaction, valorRealizado: Long, dataRealizada: Long): Transaction? {
        if (transaction.status !in setOf(TransactionStatus.PREVISTA, TransactionStatus.VENCENDO, TransactionStatus.ATRASADA)) {
            return null
        }
        return transaction.copy(
            valorRealizado = valorRealizado,
            dataRealizada = dataRealizada,
            status = TransactionStatus.CONFIRMADA
        )
    }
}
