package br.com.monitorfan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.monitorfan.data.local.entity.UsuarioEntity
import br.com.monitorfan.data.local.database.AppDatabase
import br.com.monitorfan.data.remote.RetrofitClient
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Ocioso : LoginState()
    object Carregando : LoginState()
    data class Sucesso(val usuario: Usuario) : LoginState()
    data class Erro(val mensagem: String) : LoginState()
}

sealed class CadastroState {
    object Ocioso : CadastroState()
    object Carregando : CadastroState()
    object Sucesso : CadastroState()
    data class Erro(val mensagem: String) : CadastroState()
}

sealed class RedefinirSenhaState {
    object Ocioso : RedefinirSenhaState()
    object Carregando : RedefinirSenhaState()
    object Sucesso : RedefinirSenhaState()
    data class Erro(val mensagem: String) : RedefinirSenhaState()
}

class AuthViewModel(private val repository: MonitorFanRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Ocioso)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _cadastroState = MutableStateFlow<CadastroState>(CadastroState.Ocioso)
    val cadastroState: StateFlow<CadastroState> = _cadastroState.asStateFlow()

    private val _redefinirSenhaState = MutableStateFlow<RedefinirSenhaState>(RedefinirSenhaState.Ocioso)
    val redefinirSenhaState: StateFlow<RedefinirSenhaState> = _redefinirSenhaState.asStateFlow()

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _loginState.value = LoginState.Erro("Preencha e-mail e senha.")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Carregando
            val usuario = repository.autenticar(email, senha)
            if (usuario == null) {
                _loginState.value = LoginState.Erro("E-mail ou senha inválidos.")
            } else {
                Repositorio.usuarioLogado.value = usuario
                _loginState.value = LoginState.Sucesso(usuario)
            }
        }
    }

    fun cadastrar(
        nome: String,
        email: String,
        senha: String,
        confirmarSenha: String,
        curso: String,
        matricula: String
    ) {
        val erroValidacao = validar(nome, email, senha, confirmarSenha, curso, matricula)
        if (erroValidacao != null) {
            _cadastroState.value = CadastroState.Erro(erroValidacao)
            return
        }
        viewModelScope.launch {
            _cadastroState.value = CadastroState.Carregando
            if (repository.emailJaCadastrado(email)) {
                _cadastroState.value = CadastroState.Erro("Já existe uma conta com esse e-mail.")
                return@launch
            }
            repository.inserirUsuario(
                UsuarioEntity(
                    nome = nome.trim(),
                    email = email.trim().lowercase(),
                    senha = senha,
                    curso = curso,
                    matricula = matricula.trim(),
                    cargo = Cargo.USUARIO.name
                )
            )
            _cadastroState.value = CadastroState.Sucesso
        }
    }

    fun logout() {
        Repositorio.encerrarSessao()
        _loginState.value = LoginState.Ocioso
    }

    fun redefinirSenha(email: String, matricula: String, novaSenha: String, confirmar: String) {
        if (email.isBlank() || matricula.isBlank()) {
            _redefinirSenhaState.value = RedefinirSenhaState.Erro("Preencha e-mail e matrícula.")
            return
        }
        if (novaSenha.length < 6) {
            _redefinirSenhaState.value = RedefinirSenhaState.Erro("A senha deve ter pelo menos 6 caracteres.")
            return
        }
        if (novaSenha != confirmar) {
            _redefinirSenhaState.value = RedefinirSenhaState.Erro("As senhas não coincidem.")
            return
        }
        viewModelScope.launch {
            _redefinirSenhaState.value = RedefinirSenhaState.Carregando
            val sucesso = repository.redefinirSenha(email, matricula, novaSenha)
            _redefinirSenhaState.value = if (sucesso) RedefinirSenhaState.Sucesso
            else RedefinirSenhaState.Erro("E-mail ou matrícula não encontrados.")
        }
    }

    fun resetLoginState() { _loginState.value = LoginState.Ocioso }
    fun resetCadastroState() { _cadastroState.value = CadastroState.Ocioso }
    fun resetRedefinirSenhaState() { _redefinirSenhaState.value = RedefinirSenhaState.Ocioso }

    private fun validar(
        nome: String, email: String, senha: String,
        confirmarSenha: String, curso: String, matricula: String
    ): String? {
        if (nome.isBlank()) return "Informe seu nome."
        if (curso.isBlank()) return "Selecione seu curso."
        if (matricula.isBlank()) return "Informe sua matrícula."
        if (email.isBlank() || !email.contains("@")) return "Informe um e-mail válido."
        if (senha.length < 6) return "A senha precisa ter pelo menos 6 caracteres."
        if (senha != confirmarSenha) return "As senhas não coincidem."
        return null
    }
}

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
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
        return AuthViewModel(repo) as T
    }
}
