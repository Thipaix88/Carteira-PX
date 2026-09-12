package br.com.carteirapx.data.repository

import br.com.carteirapx.data.dao.RecurringRuleDao
import br.com.carteirapx.data.entity.RecurringRule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RecurringRuleRepository {
    fun observeActive(): Flow<List<RecurringRule>>
    suspend fun getById(id: Long): RecurringRule?
    suspend fun upsert(rule: RecurringRule): Long
}

class RecurringRuleRepositoryImpl @Inject constructor(
    private val dao: RecurringRuleDao
) : RecurringRuleRepository {
    override fun observeActive() = dao.observeActive()
    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun upsert(rule: RecurringRule) = dao.upsert(rule)
}
