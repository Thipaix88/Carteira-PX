package br.com.carteirapx.ui.extrato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.TransactionRepository
import br.com.carteirapx.ui.common.TransactionUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExtratoViewModel @Inject constructor(
    transactionRepo: TransactionRepository,
    categoryRepo: CategoryRepository
) : ViewModel() {

    val items = combine(transactionRepo.observeAll(), categoryRepo.observeAll()) { txs, cats ->
        val byId = cats.associateBy { it.id }
        txs.map { t ->
            val c = byId[t.categoryId]
            TransactionUi(t, c?.name ?: "Sem categoria", c?.iconName ?: "category")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
