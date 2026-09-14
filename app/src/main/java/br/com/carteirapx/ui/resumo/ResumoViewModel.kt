package br.com.carteirapx.ui.resumo

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.entity.TransactionType
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.TransactionRepository
import br.com.carteirapx.ui.common.TransactionUi
import br.com.carteirapx.util.monthRange
import br.com.carteirapx.util.monthShortLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CategorySlice(val categoryId: Long, val name: String, val valor: Long, val color: Color)
data class MonthBar(val label: String, val valor: Long)

data class ResumoUiState(
    val totalMes: Long = 0,
    val fatias: List<CategorySlice> = emptyList(),
    val meses: List<MonthBar> = emptyList(),
    val transacoesMes: List<TransactionUi> = emptyList()
)

private val PALETTE = listOf(
    Color(0xFF00C853), Color(0xFF2E7D32), Color(0xFFFFC107), Color(0xFF1976D2),
    Color(0xFFE53935), Color(0xFF8E24AA), Color(0xFF546E7A), Color(0xFFFF7043)
)

@HiltViewModel
class ResumoViewModel @Inject constructor(
    transactionRepo: TransactionRepository,
    categoryRepo: CategoryRepository
) : ViewModel() {

    val uiState = combine(transactionRepo.observeAll(), categoryRepo.observeAll()) { txs, cats ->
        val byId = cats.associateBy { it.id }
        val valid = txs.filter { it.status != TransactionStatus.CANCELADA }

        val (start, end) = monthRange(0)
        val esteMes = valid.filter { it.type == TransactionType.DESPESA && it.dataPrevista in start..end }
        val transacoesMes = esteMes.map { t ->
            val c = byId[t.categoryId]
            TransactionUi(t, c?.name ?: "Sem categoria", c?.iconName ?: "category")
        }

        val porCategoria = esteMes.groupBy { it.categoryId }
            .mapValues { (_, list) -> list.sumOf { it.valorRealizado ?: it.valorPrevisto } }
            .entries.sortedByDescending { it.value }

        val total = porCategoria.sumOf { it.value }
        val fatias = porCategoria.mapIndexed { i, (catId, valor) ->
            CategorySlice(catId, byId[catId]?.name ?: "Outros", valor, PALETTE[i % PALETTE.size])
        }

        val meses = (5 downTo 0).map { mesesAtras ->
            val (s, e) = monthRange(-mesesAtras)
            val valor = valid.filter { it.type == TransactionType.DESPESA && it.dataPrevista in s..e }
                .sumOf { it.valorRealizado ?: it.valorPrevisto }
            MonthBar(monthShortLabel(-mesesAtras), valor)
        }

        ResumoUiState(totalMes = total, fatias = fatias, meses = meses, transacoesMes = transacoesMes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumoUiState())
}
