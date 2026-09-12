package br.com.carteirapx.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.carteirapx.data.dao.AccountDao
import br.com.carteirapx.data.dao.CategoryDao
import br.com.carteirapx.data.dao.RecurringRuleDao
import br.com.carteirapx.data.dao.TransactionDao
import br.com.carteirapx.data.entity.Account
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.Goal
import br.com.carteirapx.data.entity.RecurringRule
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.Transfer

@Database(
    entities = [Account::class, Category::class, RecurringRule::class, Transaction::class, Transfer::class, Goal::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun transactionDao(): TransactionDao
}
