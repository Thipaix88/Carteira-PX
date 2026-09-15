package br.com.carteirapx.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.entity.TransactionType
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.TransactionRepository
import br.com.carteirapx.domain.usecase.PeriodDeficitAlert
import br.com.carteirapx.domain.usecase.PeriodDeficitAlertUseCase
import br.com.carteirapx.ui.common.TransactionUi
import br.com.carteirapx.util.monthFullLabel
import br.com.carteirapx.util.monthRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class DashboardUiState(
    val saldoReal: Long = 0,
    val saldoProjetado30d: Long = 0,
    val proximos: List<TransactionUi> = emptyList(),
    val mesOffset: Int = 0,
    val mesLabel: String = "",
    val receitasMes: Long = 0,
    val despesasMes: Long = 0,
    val saldoMes: Long = 0,
    val transacoesMes: List<TransactionUi> = emptyList(),
    val alertaPeriodo: PeriodDeficitAlert? = null
)

private data class BaseData(
    val real: Long,
    val projetado: Long,
    val proximos: List<TransactionUi>,
    val categorias: Map<Long, Category>
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    categoryRepo: CategoryRepository
) : ViewModel() {

    private val em30dias = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)
    private val mesOffset = MutableStateFlow(0)

    private val baseFlow = combine(
        transactionRepo.observeSaldoReal(),
        transactionRepo.observeSaldoProjetadoAte(em30dias),
        transactionRepo.observeProximosCompromissos(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(60), 5),
        categoryRepo.observeAll()
    ) { real, projetado, proximos, cats ->
        val byId = cats.associateBy { it.id }
        BaseData(
            real = real,
            projetado = projetado,
            proximos = proximos.map { it.toUi(byId) },
            categorias = byId
        )
    }

    val uiState = combine(baseFlow, transactionRepo.observeAll(), mesOffset) { base, allTx, offset ->
        val (start, end) = monthRange(offset)
        val doMes = allTx.filter { it.status != TransactionStatus.CANCELADA && it.dataPrevista in start..end }
        val receitas = doMes.filter { it.type == TransactionType.RECEITA }.sumOf { it.valorRealizado ?: it.valorPrevisto }
        val despesas = doMes.filter { it.type == TransactionType.DESPESA }.sumOf { it.valorRealizado ?: it.valorPrevisto }

        DashboardUiState(
            saldoReal = base.real,
            saldoProjetado30d = base.projetado,
            proximos = base.proximos,
            mesOffset = offset,
            mesLabel = monthFullLabel(offset),
            receitasMes = receitas,
            despesasMes = despesas,
            saldoMes = receitas - despesas,
            transacoesMes = doMes.map { it.toUi(base.categorias) },
            alertaPeriodo = if (offset == 0) PeriodDeficitAlertUseCase(allTx) else null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun mesAnterior() { mesOffset.value -= 1 }
    fun proximoMes() { mesOffset.value += 1 }
}

private fun Transaction.toUi(byId: Map<Long, Category>): TransactionUi {
    val c = byId[categoryId]
    return TransactionUi(this, c?.name ?: "Sem categoria", c?.iconName ?: "category")
}
