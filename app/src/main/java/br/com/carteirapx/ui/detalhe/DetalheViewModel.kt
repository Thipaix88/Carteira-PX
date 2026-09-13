package br.com.carteirapx.ui.detalhe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.entity.Transaction
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.repository.CategoryRepository
import br.com.carteirapx.data.repository.TransactionRepository
import br.com.carteirapx.domain.usecase.ConfirmTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalheViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    categoryRepo: CategoryRepository
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction = _transaction.asStateFlow()

    val categories = categoryRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun load(id: Long) {
        viewModelScope.launch { _transaction.value = transactionRepo.getById(id) }
    }

    fun confirmar(valorRealizado: Long, dataRealizada: Long = System.currentTimeMillis()) {
        val atual = _transaction.value ?: return
        val atualizado = ConfirmTransactionUseCase(atual, valorRealizado, dataRealizada) ?: return
        viewModelScope.launch {
            transactionRepo.upsert(atualizado)
            _transaction.value = atualizado
        }
    }

    fun cancelar() {
        val atual = _transaction.value ?: return
        if (atual.status == TransactionStatus.CONFIRMADA) return
        val atualizado = atual.copy(status = TransactionStatus.CANCELADA)
        viewModelScope.launch {
            transactionRepo.upsert(atualizado)
            _transaction.value = atualizado
        }
    }

    fun excluir(onDone: () -> Unit) {
        val atual = _transaction.value ?: return
        viewModelScope.launch {
            transactionRepo.delete(atual)
            onDone()
        }
    }
}
