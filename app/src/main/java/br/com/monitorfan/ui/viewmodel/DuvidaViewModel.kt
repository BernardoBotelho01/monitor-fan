package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.data.local.database.AppDatabase
import br.com.monitorfan.data.local.entity.DuvidaEntity
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.data.local.entity.RespostaEntity
import br.com.monitorfan.data.remote.RetrofitClient
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.dados.Duvida
import br.com.monitorfan.dados.Repositorio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DuvidaViewModel(private val repository: MonitorFanRepository) : ViewModel() {

    val duvidas: StateFlow<List<Duvida>> by lazy {
        val curso = Repositorio.usuarioLogado.value?.curso ?: ""
        repository.observarDuvidasDoCurso(curso)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    private val _duvidaDetalhe = MutableStateFlow<Duvida?>(null)
    val duvidaDetalhe: StateFlow<Duvida?> = _duvidaDetalhe.asStateFlow()

    private val _duvidaPublicada = MutableStateFlow(false)
    val duvidaPublicada: StateFlow<Boolean> = _duvidaPublicada.asStateFlow()

    private val _respostaEnviada = MutableStateFlow(false)
    val respostaEnviada: StateFlow<Boolean> = _respostaEnviada.asStateFlow()

    private val _duvidaDeletada = MutableStateFlow(false)
    val duvidaDeletada: StateFlow<Boolean> = _duvidaDeletada.asStateFlow()

    private val _duvidaEditada = MutableStateFlow(false)
    val duvidaEditada: StateFlow<Boolean> = _duvidaEditada.asStateFlow()

    fun carregarDuvida(duvidaId: Long) {
        viewModelScope.launch {
            _duvidaDetalhe.value = repository.buscarDuvida(duvidaId)
        }
    }

    fun criarDuvida(disciplina: String, titulo: String, descricao: String) {
        val usuario = Repositorio.usuarioLogado.value ?: return
        viewModelScope.launch {
            repository.inserirDuvida(
                DuvidaEntity(
                    autorId = usuario.id,
                    curso = usuario.curso,
                    disciplina = disciplina.trim(),
                    titulo = titulo.trim(),
                    descricao = descricao.trim(),
                    criadaEm = System.currentTimeMillis()
                )
            )
            withContext(Dispatchers.Main) {
                Repositorio.criarDuvida(
                    autorId = usuario.id,
                    curso = usuario.curso,
                    disciplina = disciplina,
                    titulo = titulo,
                    descricao = descricao
                )
                _duvidaPublicada.value = true
            }
        }
    }

    fun responderDuvida(duvidaId: Long, texto: String) {
        val usuario = Repositorio.usuarioLogado.value ?: return
        val duvidaAtual = _duvidaDetalhe.value ?: return
        if (duvidaAtual.curso != usuario.curso) return
        val podeResponder = usuario.cargo == Cargo.MONITOR ||
                            usuario.cargo == Cargo.PROFESSOR ||
                            usuario.id == duvidaAtual.autorId
        if (!podeResponder) return

        viewModelScope.launch {
            repository.inserirResposta(
                RespostaEntity(
                    duvidaId = duvidaId,
                    autorId = usuario.id,
                    texto = texto.trim(),
                    criadaEm = System.currentTimeMillis()
                )
            )
            Repositorio.responderDuvida(duvidaId, usuario, texto)
            _duvidaDetalhe.value = repository.buscarDuvida(duvidaId)
            _respostaEnviada.value = true
        }
    }

    fun deletarDuvida(duvidaId: Long) {
        viewModelScope.launch {
            repository.deletarDuvida(duvidaId)
            withContext(Dispatchers.Main) {
                Repositorio.deletarDuvida(duvidaId)
                _duvidaDetalhe.value = null
                _duvidaDeletada.value = true
            }
        }
    }

    fun editarDuvida(duvidaId: Long, titulo: String, disciplina: String, descricao: String) {
        if (titulo.isBlank() || disciplina.isBlank() || descricao.isBlank()) return
        viewModelScope.launch {
            repository.atualizarDuvida(duvidaId, titulo, disciplina, descricao)
            withContext(Dispatchers.Main) {
                Repositorio.atualizarDuvida(duvidaId, titulo, disciplina, descricao)
                _duvidaDetalhe.value = _duvidaDetalhe.value?.copy(
                    titulo = titulo.trim(),
                    disciplina = disciplina.trim(),
                    descricao = descricao.trim()
                )
                _duvidaEditada.value = true
            }
        }
    }

    fun deletarResposta(duvidaId: Long, respostaId: Long) {
        viewModelScope.launch {
            repository.deletarResposta(respostaId)
            withContext(Dispatchers.Main) {
                Repositorio.deletarResposta(duvidaId, respostaId)
                _duvidaDetalhe.value = repository.buscarDuvida(duvidaId)
            }
        }
    }

    fun editarResposta(duvidaId: Long, respostaId: Long, texto: String) {
        if (texto.isBlank()) return
        viewModelScope.launch {
            repository.atualizarResposta(respostaId, texto)
            withContext(Dispatchers.Main) {
                Repositorio.atualizarResposta(duvidaId, respostaId, texto)
                _duvidaDetalhe.value = _duvidaDetalhe.value?.let { d ->
                    d.copy(respostas = d.respostas.map { r ->
                        if (r.id == respostaId) r.copy(texto = texto.trim()) else r
                    }.toMutableList())
                }
            }
        }
    }

    fun resetDuvidaPublicada() { _duvidaPublicada.value = false }
    fun resetRespostaEnviada() { _respostaEnviada.value = false }
    fun resetDuvidaDeletada() { _duvidaDeletada.value = false }
    fun resetDuvidaEditada() { _duvidaEditada.value = false }
}

class DuvidaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
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
        return DuvidaViewModel(repo) as T
    }
}
