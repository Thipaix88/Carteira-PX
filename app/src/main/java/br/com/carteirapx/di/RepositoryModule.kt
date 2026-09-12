package br.com.carteirapx.di

import br.com.carteirapx.data.repository.AccountRepository
import br.com.carteirapx.data.repository.AccountRepositoryImpl
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.CategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository
}
