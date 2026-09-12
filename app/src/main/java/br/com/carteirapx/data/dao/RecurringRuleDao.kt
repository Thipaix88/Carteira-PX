package br.com.carteirapx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.carteirapx.data.entity.RecurringRule
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringRuleDao {
    @Query("SELECT * FROM recurring_rules WHERE active = 1 ORDER BY description")
    fun observeActive(): Flow<List<RecurringRule>>

    @Query("SELECT * FROM recurring_rules WHERE id = :id")
    suspend fun getById(id: Long): RecurringRule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rule: RecurringRule): Long

    @Update
    suspend fun update(rule: RecurringRule)
}
