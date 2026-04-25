package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.data.local.database.AppDatabase
import br.com.monitorfan.data.remote.RetrofitClient
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class EditarPerfilState {
    object Ocioso : EditarPerfilState()
    object Carregando : EditarPerfilState()
    object Sucesso : EditarPerfilState()
    data class Erro(val mensagem: String) : EditarPerfilState()
}

sealed class AlterarSenhaState {
    object Ocioso : AlterarSenhaState()
    object Carregando : AlterarSenhaState()
    object Sucesso : AlterarSenhaState()
    data class Erro(val mensagem: String) : AlterarSenhaState()
}

class PerfilViewModel(private val repository: MonitorFanRepository) : ViewModel() {

    private val _state = MutableStateFlow<EditarPerfilState>(EditarPerfilState.Ocioso)
    val state: StateFlow<EditarPerfilState> = _state.asStateFlow()

    private val _alterarSenhaState = MutableStateFlow<AlterarSenhaState>(AlterarSenhaState.Ocioso)
    val alterarSenhaState: StateFlow<AlterarSenhaState> = _alterarSenhaState.asStateFlow()

    fun salvarPerfil(
        usuario: Usuario,
        novoNome: String,
        novoEmail: String,
        novaMatricula: String,
        novoCurso: String,
        novaFotoUri: String?
    ) {
        if (novoNome.isBlank()) { _state.value = EditarPerfilState.Erro("Informe seu nome."); return }
        if (novoCurso.isBlank()) { _state.value = EditarPerfilState.Erro("Selecione seu curso."); return }
        if (novaMatricula.isBlank()) { _state.value = EditarPerfilState.Erro("Informe sua matrícula."); return }
        if (novoEmail.isBlank() || !novoEmail.contains("@")) {
            _state.value = EditarPerfilState.Erro("Informe um e-mail válido.")
            return
        }

        viewModelScope.launch {
            _state.value = EditarPerfilState.Carregando
            if (novoEmail.trim().lowercase() != usuario.email.lowercase() &&
                repository.emailJaCadastradoPorOutro(novoEmail, usuario.id)
            ) {
                _state.value = EditarPerfilState.Erro("E-mail já usado por outra conta.")
                return@launch
            }
            val atualizado = usuario.copy(
                nome = novoNome.trim(),
                email = novoEmail.trim().lowercase(),
                matricula = novaMatricula.trim(),
                curso = novoCurso,
                fotoUri = novaFotoUri
            )
            repository.atualizarPerfil(atualizado)
            withContext(Dispatchers.Main) {
                Repositorio.atualizarEmMemoria(atualizado)
            }
            _state.value = EditarPerfilState.Sucesso
        }
    }

    fun alterarSenha(usuario: Usuario, senhaAtual: String, novaSenha: String, confirmar: String) {
        if (senhaAtual.isBlank() || novaSenha.isBlank() || confirmar.isBlank()) {
            _alterarSenhaState.value = AlterarSenhaState.Erro("Preencha todos os campos.")
            return
        }
        if (novaSenha.length < 6) {
            _alterarSenhaState.value = AlterarSenhaState.Erro("A nova senha deve ter pelo menos 6 caracteres.")
            return
        }
        if (novaSenha != confirmar) {
            _alterarSenhaState.value = AlterarSenhaState.Erro("As senhas não coincidem.")
            return
        }
        viewModelScope.launch {
            _alterarSenhaState.value = AlterarSenhaState.Carregando
            val sucesso = repository.alterarSenha(usuario.id, senhaAtual, novaSenha)
            withContext(Dispatchers.Main) {
                if (sucesso) {
                    Repositorio.atualizarSenhaEmMemoria(usuario.id, novaSenha)
                    _alterarSenhaState.value = AlterarSenhaState.Sucesso
                } else {
                    _alterarSenhaState.value = AlterarSenhaState.Erro("Senha atual incorreta.")
                }
            }
        }
    }

    fun resetState() { _state.value = EditarPerfilState.Ocioso }
    fun resetAlterarSenhaState() { _alterarSenhaState.value = AlterarSenhaState.Ocioso }
}

class PerfilViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
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
        return PerfilViewModel(repo) as T
    }
}
