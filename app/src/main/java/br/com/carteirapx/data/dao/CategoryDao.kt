package br.com.carteirapx.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE archived = 0 ORDER BY name")
    fun observeAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type AND archived = 0 ORDER BY name")
    fun observeByType(type: TransactionType): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)
}
