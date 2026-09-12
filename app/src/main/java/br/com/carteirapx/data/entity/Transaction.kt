package br.com.carteirapx.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cada ocorrência real de um lançamento (avulso ou gerado a partir de uma RecurringRule).
 *
 * `recurringRuleId` nulo = lançamento avulso, sem série.
 * `valorRealizado`/`dataRealizada` só são preenchidos no momento da confirmação —
 * o saldo REAL só soma linhas com dataRealizada preenchida; o saldo PROJETADO soma
 * tudo (realizado + previsto), sempre calculado via query, nunca armazenado em campo.
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["accountId"]),
        ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"]),
        ForeignKey(entity = RecurringRule::class, parentColumns = ["id"], childColumns = ["recurringRuleId"])
    ],
    indices = [Index("accountId"), Index("categoryId"), Index("recurringRuleId"), Index("dataPrevista"), Index("status")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recurringRuleId: Long? = null,
    val type: TransactionType,
    val description: String,
    val valorPrevisto: Long, // centavos
    val valorRealizado: Long? = null, // centavos, nulo até confirmar
    val dataPrevista: Long, // epoch millis
    val dataRealizada: Long? = null, // epoch millis, nulo até confirmar
    val status: TransactionStatus = TransactionStatus.PREVISTA,
    val categoryId: Long,
    val accountId: Long,
    val notes: String = ""
)
