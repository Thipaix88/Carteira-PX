package br.com.carteirapx.di

import android.content.Context
import androidx.room.Room
import br.com.carteirapx.data.AppDatabase
import br.com.carteirapx.data.dao.AccountDao
import br.com.carteirapx.data.dao.CategoryDao
import br.com.carteirapx.data.dao.RecurringRuleDao
import br.com.carteirapx.data.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "carteirapx.db")
            .fallbackToDestructiveMigration() // aceitável só durante o MVP; trocar por migrações reais antes do lançamento
            .build()

    @Provides fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideRecurringRuleDao(db: AppDatabase): RecurringRuleDao = db.recurringRuleDao()
    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
}
