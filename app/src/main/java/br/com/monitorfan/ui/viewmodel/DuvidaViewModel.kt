package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.MonitorFanApp
import br.com.monitorfan.data.firebase.FirebaseRepository
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Duvida
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Resposta
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.snapshotFlow

class DuvidaViewModel(private val repo: FirebaseRepository) : ViewModel() {

    val duvidas: StateFlow<List<Duvida>> by lazy {
        val curso = Repositorio.usuarioLogado.value?.curso ?: ""
        repo.observarTodasDuvidas()
            .map { lista -> lista.filter { it.curso == curso }.sortedByDescending { it.criadaEm } }
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

    private var respostasJob: Job? = null

    fun carregarDuvida(duvidaId: String) {
        respostasJob?.cancel()
        respostasJob = viewModelScope.launch {
            combine(
                repo.observarRespostasDaDuvida(duvidaId),
                snapshotFlow { Repositorio.duvidas.toList() }
            ) { respostas, duvidas ->
                duvidas.firstOrNull { it.id == duvidaId }
                    ?.copy(respostas = respostas.toMutableList())
            }.collectLatest { duvida ->
                _duvidaDetalhe.value = duvida
            }
        }
    }

    fun criarDuvida(disciplina: String, titulo: String, descricao: String) {
        val usuario = Repositorio.usuarioLogado.value ?: return
        viewModelScope.launch {
            repo.inserirDuvida(
                Duvida(
                    autorId = usuario.id,
                    curso = usuario.curso,
                    disciplina = disciplina.trim(),
                    titulo = titulo.trim(),
                    descricao = descricao.trim(),
                    criadaEm = System.currentTimeMillis()
                )
            )
            _duvidaPublicada.value = true
        }
    }

    fun responderDuvida(duvidaId: String, texto: String) {
        val usuario = Repositorio.usuarioLogado.value ?: return
        val duvidaAtual = _duvidaDetalhe.value ?: return
        if (duvidaAtual.curso != usuario.curso) return
        val podeResponder = usuario.cargo == Cargo.MONITOR ||
                            usuario.cargo == Cargo.PROFESSOR ||
                            usuario.id == duvidaAtual.autorId
        if (!podeResponder) return

        viewModelScope.launch {
            try {
                repo.inserirResposta(
                    duvidaId,
                    Resposta(
                        autorId = usuario.id,
                        texto = texto.trim(),
                        criadaEm = System.currentTimeMillis()
                    ),
                    usuario.cargo
                )
                _respostaEnviada.value = true
            } catch (e: Exception) {
                android.util.Log.e("DuvidaViewModel", "Erro ao enviar resposta: ${e.message}", e)
            }
        }
    }

    fun deletarDuvida(duvidaId: String) {
        viewModelScope.launch {
            repo.deletarDuvida(duvidaId)
            _duvidaDetalhe.value = null
            _duvidaDeletada.value = true
        }
    }

    fun editarDuvida(duvidaId: String, titulo: String, disciplina: String, descricao: String) {
        if (titulo.isBlank() || disciplina.isBlank() || descricao.isBlank()) return
        viewModelScope.launch {
            repo.atualizarDuvida(duvidaId, titulo, disciplina, descricao)
            _duvidaDetalhe.value = _duvidaDetalhe.value?.copy(
                titulo = titulo.trim(),
                disciplina = disciplina.trim(),
                descricao = descricao.trim()
            )
            _duvidaEditada.value = true
        }
    }

    fun deletarResposta(duvidaId: String, respostaId: String) {
        viewModelScope.launch { repo.deletarResposta(duvidaId, respostaId) }
    }

    fun editarResposta(duvidaId: String, respostaId: String, texto: String) {
        if (texto.isBlank()) return
        viewModelScope.launch { repo.atualizarResposta(duvidaId, respostaId, texto) }
    }

    fun resetDuvidaPublicada() { _duvidaPublicada.value = false }
    fun resetRespostaEnviada() { _respostaEnviada.value = false }
    fun resetDuvidaDeletada() { _duvidaDeletada.value = false }
    fun resetDuvidaEditada() { _duvidaEditada.value = false }
}

class DuvidaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = (context.applicationContext as MonitorFanApp).firebaseRepository
        @Suppress("UNCHECKED_CAST")
        return DuvidaViewModel(repo) as T
    }
}
