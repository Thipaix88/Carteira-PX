package br.com.carteirapx.data.repository

import br.com.carteirapx.data.dao.AccountDao
import br.com.carteirapx.data.entity.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AccountRepository {
    fun observeAll(): Flow<List<Account>>
    suspend fun getById(id: Long): Account?
    suspend fun upsert(account: Account): Long
    suspend fun delete(account: Account)
}

class AccountRepositoryImpl @Inject constructor(
    private val dao: AccountDao
) : AccountRepository {
    override fun observeAll(): Flow<List<Account>> = dao.observeAll()
    override suspend fun getById(id: Long): Account? = dao.getById(id)
    override suspend fun upsert(account: Account): Long = dao.upsert(account)
    override suspend fun delete(account: Account) = dao.delete(account)
}
