package br.com.carteirapx.domain.usecase

import br.com.carteirapx.data.entity.TransactionStatus
import java.util.Calendar

/**
 * Calcula o status de um lançamento com base apenas na data prevista comparada a hoje.
 * Nunca usado para CONFIRMADA ou CANCELADA — essas transições são explícitas (confirmação/cancelamento do usuário).
 */
object ComputeTransactionStatus {
    operator fun invoke(dataPrevista: Long, today: Long = System.currentTimeMillis()): TransactionStatus {
        val d1 = startOfDay(dataPrevista)
        val d2 = startOfDay(today)
        return when {
            d1 < d2 -> TransactionStatus.ATRASADA
            d1 == d2 -> TransactionStatus.VENCENDO
            else -> TransactionStatus.PREVISTA
        }
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
