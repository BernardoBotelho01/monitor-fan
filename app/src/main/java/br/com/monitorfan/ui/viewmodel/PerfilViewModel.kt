package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.MonitorFanApp
import br.com.monitorfan.data.firebase.FirebaseRepository
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

sealed class TrocarEmailState {
    object Ocioso : TrocarEmailState()
    object Carregando : TrocarEmailState()
    object Sucesso : TrocarEmailState()
    data class Erro(val mensagem: String) : TrocarEmailState()
}

sealed class DeletarContaState {
    object Ocioso : DeletarContaState()
    object Carregando : DeletarContaState()
    object Sucesso : DeletarContaState()
    data class Erro(val mensagem: String) : DeletarContaState()
}

class PerfilViewModel(private val repo: FirebaseRepository) : ViewModel() {

    private val _state = MutableStateFlow<EditarPerfilState>(EditarPerfilState.Ocioso)
    val state: StateFlow<EditarPerfilState> = _state.asStateFlow()

    private val _alterarSenhaState = MutableStateFlow<AlterarSenhaState>(AlterarSenhaState.Ocioso)
    val alterarSenhaState: StateFlow<AlterarSenhaState> = _alterarSenhaState.asStateFlow()

    private val _deletarContaState = MutableStateFlow<DeletarContaState>(DeletarContaState.Ocioso)
    val deletarContaState: StateFlow<DeletarContaState> = _deletarContaState.asStateFlow()

    private val _trocarEmailState = MutableStateFlow<TrocarEmailState>(TrocarEmailState.Ocioso)
    val trocarEmailState: StateFlow<TrocarEmailState> = _trocarEmailState.asStateFlow()

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
        viewModelScope.launch {
            _state.value = EditarPerfilState.Carregando
            try {
                val atualizado = usuario.copy(
                    nome = novoNome.trim(),
                    matricula = novaMatricula.trim(),
                    curso = novoCurso,
                    fotoUri = novaFotoUri
                )
                repo.atualizarPerfil(atualizado)
                withContext(Dispatchers.Main) {
                    Repositorio.atualizarEmMemoria(atualizado)
                }
                _state.value = EditarPerfilState.Sucesso
            } catch (e: Exception) {
                _state.value = EditarPerfilState.Erro("Erro ao salvar perfil. Tente novamente.")
            }
        }
    }

    fun alterarSenha(senhaAtual: String, novaSenha: String, confirmar: String) {
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
            try {
                repo.alterarSenha(senhaAtual, novaSenha)
                _alterarSenhaState.value = AlterarSenhaState.Sucesso
            } catch (e: Exception) {
                _alterarSenhaState.value = AlterarSenhaState.Erro("Senha atual incorreta.")
            }
        }
    }

    fun deletarConta(senha: String) {
        if (senha.isBlank()) {
            _deletarContaState.value = DeletarContaState.Erro("Informe sua senha para confirmar.")
            return
        }
        viewModelScope.launch {
            _deletarContaState.value = DeletarContaState.Carregando
            try {
                val uid = Repositorio.usuarioLogado.value?.id ?: ""
                val sucesso = repo.deletarConta(senha)
                if (sucesso) {
                    withContext(Dispatchers.Main) {
                        Repositorio.removerUsuarioEmMemoria(uid)
                    }
                    _deletarContaState.value = DeletarContaState.Sucesso
                } else {
                    _deletarContaState.value = DeletarContaState.Erro("Erro ao deletar conta.")
                }
            } catch (e: Exception) {
                _deletarContaState.value = DeletarContaState.Erro("Senha incorreta.")
            }
        }
    }

    fun fazerLogout() {
        repo.logout()
        Repositorio.encerrarSessao()
    }

    fun solicitarTrocaEmail(novoEmail: String) {
        if (novoEmail.isBlank() || !novoEmail.contains("@")) {
            _trocarEmailState.value = TrocarEmailState.Erro("Informe um e-mail válido.")
            return
        }
        viewModelScope.launch {
            _trocarEmailState.value = TrocarEmailState.Carregando
            try {
                repo.solicitarTrocaEmail(novoEmail)
                _trocarEmailState.value = TrocarEmailState.Sucesso
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("CREDENTIAL_TOO_OLD") == true ||
                    e.message?.contains("recent") == true ->
                        "Sessão expirada. Saia, entre novamente e tente outra vez."
                    e.message?.contains("already in use") == true ->
                        "Este e-mail já está em uso por outra conta."
                    else -> "Erro ao solicitar troca. Tente novamente."
                }
                _trocarEmailState.value = TrocarEmailState.Erro(msg)
            }
        }
    }

    fun resetState() { _state.value = EditarPerfilState.Ocioso }
    fun resetAlterarSenhaState() { _alterarSenhaState.value = AlterarSenhaState.Ocioso }
    fun resetDeletarContaState() { _deletarContaState.value = DeletarContaState.Ocioso }
    fun resetTrocarEmailState() { _trocarEmailState.value = TrocarEmailState.Ocioso }
}

class PerfilViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = (context.applicationContext as MonitorFanApp).firebaseRepository
        @Suppress("UNCHECKED_CAST")
        return PerfilViewModel(repo) as T
    }
}
