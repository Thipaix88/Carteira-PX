package br.com.carteirapx.domain.usecase

import br.com.carteirapx.data.entity.RecurrenceFrequency
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.repository.RecurringRuleRepository
import br.com.carteirapx.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Garante que cada RecurringRule ativa tenha ocorrências (Transaction) geradas até um
 * horizonte de 60 dias à frente. Roda na abertura do app — quando entrarmos em notificações
 * (WorkManager + BOOT_COMPLETED), esse mesmo use case passa a rodar em segundo plano também.
 */
class GenerateRecurringOccurrencesUseCase @Inject constructor(
    private val ruleRepo: RecurringRuleRepository,
    private val transactionRepo: TransactionRepository
) {
    private val horizonMillis = TimeUnit.DAYS.toMillis(60)

    suspend operator fun invoke() {
        val today = System.currentTimeMillis()
        val horizon = today + horizonMillis
        val rules = ruleRepo.observeActive().first()

        rules.forEach { rule ->
            var lastDate = transactionRepo.getLatestByRule(rule.id)?.dataPrevista ?: rule.startDate
            var guard = 0
            while (guard < 36) {
                val next = nextDate(lastDate, rule.frequency, rule.dayOfReference)
                if (next > horizon) break
                if (rule.endDate != null && next > rule.endDate) break

                transactionRepo.upsert(
                    Transaction(
                        recurringRuleId = rule.id,
                        type = rule.type,
                        description = rule.description,
                        valorPrevisto = rule.defaultAmount,
                        dataPrevista = next,
                        status = ComputeTransactionStatus(next, today),
                        categoryId = rule.categoryId,
                        accountId = rule.accountId
                    )
                )
                lastDate = next
                guard++
            }
        }
    }

    private fun nextDate(current: Long, freq: RecurrenceFrequency, dayOfReference: Int): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = current
            set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        when (freq) {
            RecurrenceFrequency.SEMANAL -> cal.add(Calendar.DAY_OF_MONTH, 7)
            RecurrenceFrequency.QUINZENAL -> cal.add(Calendar.DAY_OF_MONTH, 14)
            RecurrenceFrequency.MENSAL -> { cal.add(Calendar.MONTH, 1); clampDay(cal, dayOfReference) }
            RecurrenceFrequency.BIMESTRAL -> { cal.add(Calendar.MONTH, 2); clampDay(cal, dayOfReference) }
            RecurrenceFrequency.TRIMESTRAL -> { cal.add(Calendar.MONTH, 3); clampDay(cal, dayOfReference) }
            RecurrenceFrequency.SEMESTRAL -> { cal.add(Calendar.MONTH, 6); clampDay(cal, dayOfReference) }
            RecurrenceFrequency.ANUAL -> cal.add(Calendar.YEAR, 1)
        }
        return cal.timeInMillis
    }

    /** Se o dia de referência (ex: 31) não existir no mês (ex: fevereiro), usa o último dia do mês. */
    private fun clampDay(cal: Calendar, day: Int) {
        val max = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, minOf(day, max))
    }
}
