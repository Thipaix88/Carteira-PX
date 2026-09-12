package br.com.carteirapx.ui.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carteirapx.data.entity.Category
import br.com.carteirapx.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriasViewModel @Inject constructor(
    private val repo: CategoryRepository
) : ViewModel() {

    val categories = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun save(category: Category) {
        viewModelScope.launch { repo.upsert(category) }
    }

    fun delete(category: Category) {
        viewModelScope.launch { repo.delete(category) }
    }
}
