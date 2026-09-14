package br.com.carteirapx.data.repository

import br.com.carteirapx.data.dao.TransactionDao
import br.com.carteirapx.data.entity.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TransactionRepository {
    fun observeAll(): Flow<List<Transaction>>
    fun observeByPeriod(start: Long, end: Long): Flow<List<Transaction>>
    fun observeProximosCompromissos(from: Long, limit: Int = 10): Flow<List<Transaction>>
    suspend fun getById(id: Long): Transaction?
    suspend fun getLatestByRule(ruleId: Long): Transaction?
    suspend fun refreshStatuses(today: Long = System.currentTimeMillis())
    suspend fun upsert(transaction: Transaction): Long
    suspend fun delete(transaction: Transaction)
    fun observeSaldoReal(): Flow<Long>
    fun observeSaldoProjetado(): Flow<Long>
    fun observeSaldoProjetadoAte(until: Long): Flow<Long>
}

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun observeAll() = dao.observeAll()
    override fun observeByPeriod(start: Long, end: Long) = dao.observeByPeriod(start, end)
    override fun observeProximosCompromissos(from: Long, limit: Int) = dao.observeProximosCompromissos(from, limit)
    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun getLatestByRule(ruleId: Long) = dao.getLatestByRule(ruleId)
    override suspend fun refreshStatuses(today: Long) = dao.refreshStatuses(today)
    override suspend fun upsert(transaction: Transaction) = dao.upsert(transaction)
    override suspend fun delete(transaction: Transaction) = dao.delete(transaction)
    override fun observeSaldoReal() = dao.observeSaldoReal()
    override fun observeSaldoProjetado() = dao.observeSaldoProjetado()
    override fun observeSaldoProjetadoAte(until: Long) = dao.observeSaldoProjetadoAte(until)
}
