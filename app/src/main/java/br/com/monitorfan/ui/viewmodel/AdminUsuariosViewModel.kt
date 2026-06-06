package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.MonitorFanApp
import br.com.monitorfan.data.firebase.FirebaseRepository
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminUsuariosViewModel(private val repo: FirebaseRepository) : ViewModel() {

    val usuarios: StateFlow<List<Usuario>> = repo
        .observarTodosUsuarios()
        .map { lista ->
            val adminId = Repositorio.usuarioLogado.value?.id
            lista.filter { it.id != adminId }.sortedBy { it.nome }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun alterarCargo(usuarioId: String, novoCargo: Cargo) {
        viewModelScope.launch {
            repo.atualizarCargo(usuarioId, novoCargo)
        }
    }
}

class AdminUsuariosViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = (context.applicationContext as MonitorFanApp).firebaseRepository
        @Suppress("UNCHECKED_CAST")
        return AdminUsuariosViewModel(repo) as T
    }
}
