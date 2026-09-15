package br.com.carteirapx.ui.lancar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.entity.Account
import br.com.carteirapx.data.entity.AccountType
import br.com.carteirapx.data.entity.RecurrenceFrequency
import br.com.carteirapx.data.entity.RecurringRule
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionType
import br.com.carteirapx.data.repository.AccountRepository
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.RecurringRuleRepository
import br.com.carteirapx.data.repository.TransactionRepository
import br.com.carteirapx.domain.usecase.ComputeTransactionStatus
import br.com.carteirapx.domain.usecase.GenerateRecurringOccurrencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class LancarViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val accountRepo: AccountRepository,
    private val transactionRepo: TransactionRepository,
    private val ruleRepo: RecurringRuleRepository,
    private val generateRecurring: GenerateRecurringOccurrencesUseCase
) : ViewModel() {

    val categories = categoryRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun salvar(
        type: TransactionType,
        description: String,
        amountCentavos: Long,
        categoryId: Long,
        dateMillis: Long,
        recurrence: RecurrenceFrequency?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val account = obterOuCriarContaGeral()
            val status = ComputeTransactionStatus(dateMillis)

            val recurringRuleId = if (recurrence != null) {
                ruleRepo.upsert(
                    RecurringRule(
                        description = description,
                        type = type,
                        defaultAmount = amountCentavos,
                        categoryId = categoryId,
                        accountId = account.id,
                        frequency = recurrence,
                        dayOfReference = dayOfMonth(dateMillis),
                        startDate = dateMillis
                    )
                )
            } else null

            transactionRepo.upsert(
                Transaction(
                    recurringRuleId = recurringRuleId,
                    type = type,
                    description = description,
                    valorPrevisto = amountCentavos,
                    dataPrevista = dateMillis,
                    status = status,
                    categoryId = categoryId,
                    accountId = account.id
                )
            )
            if (recurringRuleId != null) generateRecurring()
            onDone()
        }
    }

    private suspend fun obterOuCriarContaGeral(): Account {
        val existing = accountRepo.observeAll().first().firstOrNull()
        if (existing != null) return existing
        val id = accountRepo.upsert(Account(name = "Geral", type = AccountType.CARTEIRA, initialBalance = 0))
        return Account(id = id, name = "Geral", type = AccountType.CARTEIRA, initialBalance = 0)
    }

    private fun dayOfMonth(millis: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return cal.get(Calendar.DAY_OF_MONTH)
    }
}
