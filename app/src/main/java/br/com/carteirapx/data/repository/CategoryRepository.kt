package br.com.carteirapx.data.repository

import br.com.carteirapx.data.dao.CategoryDao
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    fun observeByType(type: TransactionType): Flow<List<Category>>
    suspend fun upsert(category: Category): Long
    suspend fun delete(category: Category)
}

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {
    override fun observeAll(): Flow<List<Category>> = dao.observeAll()
    override fun observeByType(type: TransactionType): Flow<List<Category>> = dao.observeByType(type)
    override suspend fun upsert(category: Category): Long = dao.upsert(category)
    override suspend fun delete(category: Category) = dao.delete(category)
}
