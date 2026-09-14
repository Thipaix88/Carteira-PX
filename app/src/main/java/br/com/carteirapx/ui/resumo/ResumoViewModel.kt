package br.com.carteirapx.ui.resumo

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.entity.TransactionType
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class CategorySlice(val name: String, val valor: Long, val color: Color)
data class MonthBar(val label: String, val valor: Long)

data class ResumoUiState(
    val totalMes: Long = 0,
    val fatias: List<CategorySlice> = emptyList(),
    val meses: List<MonthBar> = emptyList()
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
        val porCategoria = esteMes.groupBy { it.categoryId }
            .mapValues { (_, list) -> list.sumOf { it.valorRealizado ?: it.valorPrevisto } }
            .entries.sortedByDescending { it.value }

        val total = porCategoria.sumOf { it.value }
        val fatias = porCategoria.mapIndexed { i, (catId, valor) ->
            CategorySlice(byId[catId]?.name ?: "Outros", valor, PALETTE[i % PALETTE.size])
        }

        val meses = (5 downTo 0).map { mesesAtras ->
            val (s, e) = monthRange(-mesesAtras)
            val label = monthLabel(mesesAtras)
            val valor = valid.filter { it.type == TransactionType.DESPESA && it.dataPrevista in s..e }
                .sumOf { it.valorRealizado ?: it.valorPrevisto }
            MonthBar(label, valor)
        }

        ResumoUiState(totalMes = total, fatias = fatias, meses = meses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumoUiState())
}

/** Intervalo do mês, deslocado por [offsetMeses] a partir do mês atual (negativo = meses passados). */
private fun monthRange(offsetMeses: Int): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, offsetMeses)
    cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
    val start = cal.timeInMillis
    cal.add(Calendar.MONTH, 1); cal.add(Calendar.MILLISECOND, -1)
    return start to cal.timeInMillis
}

private fun monthLabel(mesesAtras: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, -mesesAtras)
    return SimpleDateFormat("MMM", Locale("pt", "BR")).format(cal.time).replaceFirstChar { it.uppercase() }
}
