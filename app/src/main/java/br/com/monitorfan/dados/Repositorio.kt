package br.com.monitorfan.dados

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import br.com.monitorfan.data.firebase.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Repositorio {

    private var _scope: CoroutineScope? = null

    val usuarios = mutableStateListOf<Usuario>()
    val monitorias = mutableStateListOf<Monitoria>()
    val duvidas = mutableStateListOf<Duvida>()

    val usuarioLogado = mutableStateOf<Usuario?>(null)
    val isInicializado = mutableStateOf(false)

    fun inicializar(scope: CoroutineScope, repo: FirebaseRepository) {
        _scope = scope

        scope.launch {
            repo.observarTodosUsuarios()
                .retryWhen { _, attempt -> delay(2000L * (attempt + 1).coerceAtMost(5)); true }
                .collectLatest { lista ->
                    withContext(Dispatchers.Main) {
                        usuarios.clear()
                        usuarios.addAll(lista)
                    }
                }
        }

        scope.launch {
            repo.observarTodasMonitorias()
                .retryWhen { _, attempt -> delay(2000L * (attempt + 1).coerceAtMost(5)); true }
                .collectLatest { lista ->
                    withContext(Dispatchers.Main) {
                        monitorias.clear()
                        monitorias.addAll(lista)
                    }
                }
        }

        scope.launch {
            repo.observarTodasDuvidas()
                .retryWhen { _, attempt -> delay(2000L * (attempt + 1).coerceAtMost(5)); true }
                .collectLatest { lista ->
                    withContext(Dispatchers.Main) {
                        duvidas.clear()
                        duvidas.addAll(lista)
                    }
                }
        }
    }

    fun encerrarSessao() {
        usuarioLogado.value = null
    }

    fun removerUsuarioEmMemoria(usuarioId: String) {
        usuarios.removeAll { it.id == usuarioId }
        duvidas.removeAll { it.autorId == usuarioId }
        duvidas.forEach { duvida -> duvida.respostas.removeAll { it.autorId == usuarioId } }
        monitorias.removeAll { it.monitorId == usuarioId }
        if (usuarioLogado.value?.id == usuarioId) usuarioLogado.value = null
    }

    fun atualizarEmMemoria(usuario: Usuario) {
        val indice = usuarios.indexOfFirst { it.id == usuario.id }
        if (indice >= 0) usuarios[indice] = usuario
        if (usuarioLogado.value?.id == usuario.id) usuarioLogado.value = usuario
    }

    fun buscarUsuario(id: String): Usuario? = usuarios.firstOrNull { it.id == id }

    fun listarUsuariosPorCurso(curso: String): List<Usuario> =
        usuarios.filter { it.curso == curso && it.cargo != Cargo.ADMIN }

    fun responsaveisDoCurso(curso: String): List<Usuario> =
        usuarios.filter { it.curso == curso && (it.cargo == Cargo.MONITOR || it.cargo == Cargo.PROFESSOR) }

    fun monitoriasDoCurso(curso: String): List<Monitoria> =
        monitorias.filter { it.curso == curso }

    fun duvidasDoCurso(curso: String): List<Duvida> =
        duvidas.filter { it.curso == curso }.sortedByDescending { it.criadaEm }

    fun buscarDuvida(id: String): Duvida? = duvidas.firstOrNull { it.id == id }
}
