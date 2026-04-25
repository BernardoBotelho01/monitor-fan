package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.data.local.database.AppDatabase
import br.com.monitorfan.data.remote.RetrofitClient
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminUsuariosViewModel(private val repository: MonitorFanRepository) : ViewModel() {

    val usuarios: StateFlow<List<Usuario>> = repository
        .observarTodosUsuarios()
        .map { lista ->
            val adminId = Repositorio.usuarioLogado.value?.id
            lista.filter { it.id != adminId }.sortedBy { it.nome }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun alterarCargo(usuarioId: Long, novoCargo: Cargo) {
        viewModelScope.launch {
            repository.atualizarCargo(usuarioId, novoCargo)
            // Mantém o Repositorio em sincronia para as telas legadas
            Repositorio.alterarCargo(usuarioId, novoCargo)
        }
    }
}

class AdminUsuariosViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        val repo = MonitorFanRepository(
            usuarioDao = db.usuarioDao(),
            monitoriaDao = db.monitoriaDao(),
            duvidaDao = db.duvidaDao(),
            respostaDao = db.respostaDao(),
            apiService = RetrofitClient.apiService
        )
        @Suppress("UNCHECKED_CAST")
        return AdminUsuariosViewModel(repo) as T
    }
}
