package br.com.carteirapx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Define o PADRÃO de uma recorrência (ex: "Aluguel, R$1500, dia 10, mensal").
 * Nunca é reescrita quando o usuário edita uma instância (Transaction) individual —
 * isso garante que ajustar um mês não bagunça a série inteira nem meses futuros/passados.
 */
@Entity(tableName = "recurring_rules")
data class RecurringRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val type: TransactionType,
    val defaultAmount: Long, // centavos
    val categoryId: Long,
    val accountId: Long,
    val frequency: RecurrenceFrequency,
    val dayOfReference: Int, // dia do mês (1-31) ou dia da semana (1-7), conforme frequency
    val startDate: Long, // epoch millis
    val endDate: Long? = null, // nulo = sem previsão de término
    val active: Boolean = true
)
