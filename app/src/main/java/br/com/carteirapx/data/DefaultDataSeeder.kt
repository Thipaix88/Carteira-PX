package br.com.carteirapx.data

import br.com.carteirapx.data.dao.AccountDao
import br.com.carteirapx.data.dao.CategoryDao
import br.com.carteirapx.data.entity.Account
import br.com.carteirapx.data.entity.AccountType
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.TransactionType
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Cria, na primeira abertura, uma conta única "Geral" (o usuário não precisa gerenciar
 * contas por enquanto — só interessa o total) e um conjunto de categorias padrão de mercado,
 * para o app já vir usável sem exigir cadastro manual antes de lançar algo.
 */
class DefaultDataSeeder @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao
) {
    suspend fun seedIfNeeded() {
        if (accountDao.observeAll().first().isEmpty()) {
            accountDao.upsert(Account(name = "Geral", type = AccountType.CARTEIRA, initialBalance = 0))
        }

        if (categoryDao.observeAll().first().isEmpty()) {
            val despesas = listOf(
                "Moradia" to "home",
                "Alimentação" to "food",
                "Transporte" to "car",
                "Saúde" to "health",
                "Educação" to "school",
                "Lazer" to "movie",
                "Compras" to "shopping",
                "Contas e Serviços" to "bills",
                "Cartão de Crédito" to "bills",
                "Mercado" to "market",
                "Pets" to "pet",
                "Outros" to "category"
            )
            val receitas = listOf(
                "Salário" to "salary",
                "Freelance / Extra" to "salary",
                "Investimentos" to "salary",
                "Presente" to "gift",
                "Outros" to "category"
            )
            despesas.forEach { (name, icon) ->
                categoryDao.upsert(Category(name = name, type = TransactionType.DESPESA, iconName = icon))
            }
            receitas.forEach { (name, icon) ->
                categoryDao.upsert(Category(name = name, type = TransactionType.RECEITA, iconName = icon))
            }
        }
    }
}
