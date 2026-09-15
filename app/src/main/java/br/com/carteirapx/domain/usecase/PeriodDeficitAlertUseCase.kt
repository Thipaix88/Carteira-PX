package br.com.carteirapx.domain.usecase

import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.entity.TransactionType
import java.util.concurrent.TimeUnit

data class PeriodDeficitAlert(val valorExcedente: Long, val inicio: Long, val fim: Long)

/**
 * O "período" do usuário não é o mês do calendário — é o intervalo entre um recebimento (receita)
 * e o próximo. Compara despesas x receitas dentro desse intervalo e alerta se as despesas
 * ultrapassarem as receitas ali dentro, mesmo que o saldo do mês inteiro feche no azul depois.
 */
object PeriodDeficitAlertUseCase {
    operator fun invoke(allTransactions: List<Transaction>, today: Long = System.currentTimeMillis()): PeriodDeficitAlert? {
        val receitas = allTransactions
            .filter { it.type == TransactionType.RECEITA && it.status != TransactionStatus.CANCELADA }
            .sortedBy { it.dataPrevista }
        if (receitas.isEmpty()) return null

        val inicio = receitas.lastOrNull { it.dataPrevista <= today }?.dataPrevista ?: receitas.first().dataPrevista
        val fim = receitas.firstOrNull { it.dataPrevista > inicio }?.dataPrevista ?: (inicio + TimeUnit.DAYS.toMillis(30))

        val despesasPeriodo = allTransactions
            .filter { it.type == TransactionType.DESPESA && it.status != TransactionStatus.CANCELADA && it.dataPrevista in inicio until fim }
            .sumOf { it.valorRealizado ?: it.valorPrevisto }

        val receitasPeriodo = allTransactions
            .filter { it.type == TransactionType.RECEITA && it.status != TransactionStatus.CANCELADA && it.dataPrevista in inicio until fim }
            .sumOf { it.valorRealizado ?: it.valorPrevisto }

        val excedente = despesasPeriodo - receitasPeriodo
        return if (excedente > 0) PeriodDeficitAlert(excedente, inicio, fim) else null
    }
}
