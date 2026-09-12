package br.com.carteirapx.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.dao.TransactionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class DashboardUiState(
    val saldoReal: Long = 0,
    val saldoProjetado30d: Long = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionDao: TransactionDao
) : ViewModel() {

    private val em30dias = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)

    val uiState = combine(
        transactionDao.observeSaldoReal(),
        transactionDao.observeSaldoProjetadoAte(em30dias)
    ) { real, projetado ->
        DashboardUiState(saldoReal = real, saldoProjetado30d = projetado)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
