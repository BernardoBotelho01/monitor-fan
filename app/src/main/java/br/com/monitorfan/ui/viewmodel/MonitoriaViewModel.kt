package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.MonitorFanApp
import br.com.monitorfan.data.firebase.FirebaseRepository
import br.com.monitorfan.dados.Monitoria
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonitoriaViewModel(private val repo: FirebaseRepository) : ViewModel() {

    val todasMonitorias: StateFlow<List<Monitoria>> = repo
        .observarTodasMonitorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val monitoriasDoCurso: StateFlow<List<Monitoria>> by lazy {
        val curso = Repositorio.usuarioLogado.value?.curso ?: ""
        repo.observarTodasMonitorias()
            .map { lista -> lista.filter { it.curso == curso } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    private val _responsaveisDisponiveis = MutableStateFlow<List<Usuario>>(emptyList())
    val responsaveisDisponiveis: StateFlow<List<Usuario>> = _responsaveisDisponiveis.asStateFlow()

    fun carregarResponsaveisDoCurso(curso: String) {
        _responsaveisDisponiveis.value = Repositorio.responsaveisDoCurso(curso)
    }

    fun cadastrarMonitoria(
        monitorId: String, disciplina: String, curso: String,
        diaSemana: String, horario: String, sala: String
    ) {
        viewModelScope.launch {
            repo.inserirMonitoria(
                Monitoria(
                    monitorId = monitorId,
                    disciplina = disciplina.trim(),
                    curso = curso,
                    diaSemana = diaSemana,
                    horario = horario.trim(),
                    sala = sala.trim()
                )
            )
        }
    }

    fun removerMonitoria(id: String) {
        viewModelScope.launch { repo.removerMonitoria(id) }
    }

    fun atualizarMonitoria(
        id: String, monitorId: String, disciplina: String,
        curso: String, diaSemana: String, horario: String, sala: String
    ) {
        viewModelScope.launch {
            repo.atualizarMonitoria(id, monitorId, disciplina, curso, diaSemana, horario, sala)
        }
    }
}

class MonitoriaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = (context.applicationContext as MonitorFanApp).firebaseRepository
        @Suppress("UNCHECKED_CAST")
        return MonitoriaViewModel(repo) as T
    }
}
