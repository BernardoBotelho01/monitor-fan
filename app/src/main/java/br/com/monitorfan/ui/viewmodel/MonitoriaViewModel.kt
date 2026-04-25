package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.data.local.database.AppDatabase
import br.com.monitorfan.data.local.entity.MonitoriaEntity
import br.com.monitorfan.data.remote.RetrofitClient
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.dados.Monitoria
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonitoriaViewModel(private val repository: MonitorFanRepository) : ViewModel() {

    // Todas as monitorias — para uso no painel admin
    val todasMonitorias: StateFlow<List<Monitoria>> = repository
        .observarTodasMonitorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Monitorias do curso do usuário logado — para Home
    val monitoriasDoCurso: StateFlow<List<Monitoria>> by lazy {
        val curso = Repositorio.usuarioLogado.value?.curso ?: ""
        repository.observarMonitoriasDoCurso(curso)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    private val _responsaveisDisponiveis = MutableStateFlow<List<Usuario>>(emptyList())
    val responsaveisDisponiveis: StateFlow<List<Usuario>> = _responsaveisDisponiveis.asStateFlow()

    fun carregarResponsaveisDoCurso(curso: String) {
        viewModelScope.launch {
            _responsaveisDisponiveis.value = repository.listarResponsaveisDoCurso(curso)
        }
    }

    fun cadastrarMonitoria(
        monitorId: Long,
        disciplina: String,
        curso: String,
        diaSemana: String,
        horario: String,
        sala: String
    ) {
        viewModelScope.launch {
            val id = repository.inserirMonitoria(
                MonitoriaEntity(
                    monitorId = monitorId,
                    disciplina = disciplina.trim(),
                    curso = curso,
                    diaSemana = diaSemana,
                    horario = horario.trim(),
                    sala = sala.trim()
                )
            )
            // Mantém o Repositorio in-memory em sincronia
            Repositorio.cadastrarMonitoria(monitorId, disciplina, curso, diaSemana, horario, sala)
        }
    }

    fun removerMonitoria(id: Long) {
        viewModelScope.launch {
            repository.removerMonitoria(id)
            Repositorio.removerMonitoria(id)
        }
    }

    fun atualizarMonitoria(
        id: Long, monitorId: Long, disciplina: String,
        curso: String, diaSemana: String, horario: String, sala: String
    ) {
        viewModelScope.launch {
            repository.atualizarMonitoria(id, monitorId, disciplina, curso, diaSemana, horario, sala)
            Repositorio.atualizarMonitoria(id, monitorId, disciplina, curso, diaSemana, horario, sala)
        }
    }
}

class MonitoriaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
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
        return MonitoriaViewModel(repo) as T
    }
}
