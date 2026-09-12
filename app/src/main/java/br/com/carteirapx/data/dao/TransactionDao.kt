package br.com.carteirapx.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE status != 'CANCELADA' ORDER BY dataPrevista DESC")
    fun observeAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE dataPrevista BETWEEN :start AND :end AND status != 'CANCELADA' ORDER BY dataPrevista")
    fun observeByPeriod(start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE dataPrevista >= :from AND status IN ('PREVISTA','VENCENDO','ATRASADA') ORDER BY dataPrevista LIMIT :limit")
    fun observeProximosCompromissos(from: Long, limit: Int = 10): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE recurringRuleId = :ruleId ORDER BY dataPrevista")
    fun observeByRule(ruleId: Long): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    /**
     * Recalcula o status de todas as transações não confirmadas/canceladas com base na data de hoje.
     * Chamado ao abrir o app e diariamente via WorkManager — o status nunca é setado manualmente pelo usuário.
     */
    @Query(
        """
        UPDATE transactions SET status =
            CASE
                WHEN dataPrevista < :today THEN 'ATRASADA'
                WHEN dataPrevista = :today THEN 'VENCENDO'
                ELSE 'PREVISTA'
            END
        WHERE status IN ('PREVISTA','VENCENDO','ATRASADA')
        """
    )
    suspend fun refreshStatuses(today: Long)

    /** Saldo REAL: soma dos saldos iniciais das contas + tudo que já foi confirmado (dataRealizada preenchida). */
    @Query(
        """
        SELECT
            COALESCE((SELECT SUM(initialBalance) FROM accounts WHERE archived = 0), 0)
            + COALESCE((
                SELECT SUM(CASE WHEN type = 'RECEITA' THEN valorRealizado ELSE -valorRealizado END)
                FROM transactions
                WHERE dataRealizada IS NOT NULL AND status != 'CANCELADA'
            ), 0)
        """
    )
    fun observeSaldoReal(): Flow<Long>

    /** Saldo PROJETADO: saldo real + tudo que ainda está previsto (não confirmado, não cancelado). */
    @Query(
        """
        SELECT
            COALESCE((SELECT SUM(initialBalance) FROM accounts WHERE archived = 0), 0)
            + COALESCE((
                SELECT SUM(CASE WHEN type = 'RECEITA' THEN valorRealizado ELSE -valorRealizado END)
                FROM transactions
                WHERE dataRealizada IS NOT NULL AND status != 'CANCELADA'
            ), 0)
            + COALESCE((
                SELECT SUM(CASE WHEN type = 'RECEITA' THEN valorPrevisto ELSE -valorPrevisto END)
                FROM transactions
                WHERE dataRealizada IS NULL AND status != 'CANCELADA'
            ), 0)
        """
    )
    fun observeSaldoProjetado(): Flow<Long>

    /** Saldo projetado considerando só lançamentos previstos até uma data-limite (ex: hoje + 30 dias). */
    @Query(
        """
        SELECT
            COALESCE((SELECT SUM(initialBalance) FROM accounts WHERE archived = 0), 0)
            + COALESCE((
                SELECT SUM(CASE WHEN type = 'RECEITA' THEN valorRealizado ELSE -valorRealizado END)
                FROM transactions
                WHERE dataRealizada IS NOT NULL AND status != 'CANCELADA'
            ), 0)
            + COALESCE((
                SELECT SUM(CASE WHEN type = 'RECEITA' THEN valorPrevisto ELSE -valorPrevisto END)
                FROM transactions
                WHERE dataRealizada IS NULL AND status != 'CANCELADA' AND dataPrevista <= :until
            ), 0)
        """
    )
    fun observeSaldoProjetadoAte(until: Long): Flow<Long>
}
