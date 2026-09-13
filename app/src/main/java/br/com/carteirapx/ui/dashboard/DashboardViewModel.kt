package br.com.carteirapx.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.TransactionRepository
import br.com.carteirapx.ui.common.TransactionUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class DashboardUiState(
    val saldoReal: Long = 0,
    val saldoProjetado30d: Long = 0,
    val proximos: List<TransactionUi> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionRepo: TransactionRepository,
    categoryRepo: CategoryRepository
) : ViewModel() {

    private val em30dias = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)

    val uiState = combine(
        transactionRepo.observeSaldoReal(),
        transactionRepo.observeSaldoProjetadoAte(em30dias),
        transactionRepo.observeProximosCompromissos(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(60), 5),
        categoryRepo.observeAll()
    ) { real, projetado, proximos, cats ->
        val byId = cats.associateBy { it.id }
        DashboardUiState(
            saldoReal = real,
            saldoProjetado30d = projetado,
            proximos = proximos.map { t ->
                val c = byId[t.categoryId]
                TransactionUi(t, c?.name ?: "Sem categoria", c?.iconName ?: "category")
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
