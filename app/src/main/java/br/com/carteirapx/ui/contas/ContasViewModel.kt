package br.com.carteirapx.ui.contas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.entity.Account
import br.com.carteirapx.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContasViewModel @Inject constructor(
    private val repo: AccountRepository
) : ViewModel() {

    val accounts = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun save(account: Account) {
        viewModelScope.launch { repo.upsert(account) }
    }

    fun delete(account: Account) {
        viewModelScope.launch { repo.delete(account) }
    }
}
