package br.com.monitorfan.dados

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import br.com.monitorfan.data.local.entity.toDomain
import br.com.monitorfan.data.local.entity.toEntity
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.util.SenhaUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repositório de estado para a UI (Compose).
 * Mantém listas observáveis que as telas existentes consomem diretamente.
 * A partir de inicializar(), cada mutation também persiste no Room via
 * MonitorFanRepository. Os ViewModels usam MonitorFanRepository diretamente
 * via StateFlow; este objeto é mantido como bridge de compatibilidade.
 */
object Repositorio {

    private var _repository: MonitorFanRepository? = null
    private var _scope: CoroutineScope? = null

    private var proximoIdUsuario: Long = 1
    private var proximoIdMonitoria: Long = 1
    private var proximoIdDuvida: Long = 1
    private var proximoIdResposta: Long = 1

    val usuarios = mutableStateListOf<Usuario>()
    val monitorias = mutableStateListOf<Monitoria>()
    val duvidas = mutableStateListOf<Duvida>()

    val usuarioLogado = mutableStateOf<Usuario?>(null)
    val isInicializado = mutableStateOf(false)

    // ------------------------------------------------------------------
    // Inicialização com Room
    // ------------------------------------------------------------------

    fun inicializar(scope: CoroutineScope, repository: MonitorFanRepository) {
        _repository = repository
        _scope = scope

        scope.launch {
            repository.observarTodosUsuarios().collectLatest { lista ->
                withContext(Dispatchers.Main) {
                    usuarios.clear()
                    usuarios.addAll(lista)
                    if (lista.isNotEmpty()) proximoIdUsuario = lista.maxOf { it.id } + 1
                }
            }
        }

        scope.launch {
            repository.observarTodasMonitorias().collectLatest { lista ->
                withContext(Dispatchers.Main) {
                    monitorias.clear()
                    monitorias.addAll(lista)
                    if (lista.isNotEmpty()) proximoIdMonitoria = lista.maxOf { it.id } + 1
                }
            }
        }

        scope.launch {
            repository.observarTodasDuvidasComRespostas().collectLatest { lista ->
                withContext(Dispatchers.Main) {
                    duvidas.clear()
                    duvidas.addAll(lista.map { it.toDomain() })
                    if (lista.isNotEmpty()) proximoIdDuvida = lista.maxOf { it.duvida.id } + 1
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Autenticação
    // ------------------------------------------------------------------

    fun autenticar(email: String, senha: String): Usuario? {
        val encontrado = usuarios.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true) && SenhaUtils.verificar(senha, it.senha)
        }
        if (encontrado != null) usuarioLogado.value = encontrado
        return encontrado
    }

    fun encerrarSessao() {
        usuarioLogado.value = null
    }

    fun emailJaCadastrado(email: String): Boolean =
        usuarios.any { it.email.equals(email.trim(), ignoreCase = true) }

    fun cadastrar(
        nome: String,
        email: String,
        senha: String,
        curso: String,
        matricula: String
    ): Usuario {
        val novo = Usuario(
            id = proximoIdUsuario++,
            nome = nome.trim(),
            email = email.trim(),
            senha = SenhaUtils.hashear(senha),
            curso = curso,
            matricula = matricula.trim(),
            cargo = Cargo.USUARIO
        )
        usuarios.add(novo)
        _scope?.launch(Dispatchers.IO) {
            _repository?.inserirUsuario(novo.toEntity())
        }
        return novo
    }

    // ------------------------------------------------------------------
    // Administração de usuários
    // ------------------------------------------------------------------

    fun alterarCargo(usuarioId: Long, novoCargo: Cargo) {
        val indice = usuarios.indexOfFirst { it.id == usuarioId }
        if (indice >= 0) {
            val atual = usuarios[indice]
            usuarios[indice] = atual.copy(cargo = novoCargo)
            if (usuarioLogado.value?.id == usuarioId) {
                usuarioLogado.value = usuarios[indice]
            }
        }
        _scope?.launch(Dispatchers.IO) {
            _repository?.atualizarCargo(usuarioId, novoCargo)
        }
    }

    fun atualizarEmMemoria(usuario: Usuario) {
        val indice = usuarios.indexOfFirst { it.id == usuario.id }
        if (indice >= 0) usuarios[indice] = usuario
        if (usuarioLogado.value?.id == usuario.id) usuarioLogado.value = usuario
    }

    fun atualizarSenhaEmMemoria(id: Long, novaSenha: String) {
        val indice = usuarios.indexOfFirst { it.id == id }
        if (indice >= 0) {
            val atualizado = usuarios[indice].copy(senha = SenhaUtils.hashear(novaSenha))
            usuarios[indice] = atualizado
            if (usuarioLogado.value?.id == id) usuarioLogado.value = atualizado
        }
    }

    fun listarUsuariosPorCurso(curso: String): List<Usuario> =
        usuarios.filter { it.curso == curso && it.cargo != Cargo.ADMIN }

    fun buscarUsuario(id: Long): Usuario? = usuarios.firstOrNull { it.id == id }

    // ------------------------------------------------------------------
    // Monitorias
    // ------------------------------------------------------------------

    fun monitoriasDoCurso(curso: String): List<Monitoria> =
        monitorias.filter { it.curso == curso }

    fun cadastrarMonitoria(
        monitorId: Long,
        disciplina: String,
        curso: String,
        diaSemana: String,
        horario: String,
        sala: String
    ): Monitoria {
        val nova = Monitoria(
            id = proximoIdMonitoria++,
            monitorId = monitorId,
            disciplina = disciplina.trim(),
            curso = curso,
            diaSemana = diaSemana,
            horario = horario.trim(),
            sala = sala.trim()
        )
        monitorias.add(nova)
        _scope?.launch(Dispatchers.IO) {
            _repository?.inserirMonitoria(nova.toEntity())
        }
        return nova
    }

    fun removerMonitoria(id: Long) {
        monitorias.removeAll { it.id == id }
        _scope?.launch(Dispatchers.IO) {
            _repository?.removerMonitoria(id)
        }
    }

    fun atualizarMonitoria(
        id: Long, monitorId: Long, disciplina: String,
        curso: String, diaSemana: String, horario: String, sala: String
    ) {
        val indice = monitorias.indexOfFirst { it.id == id }
        if (indice >= 0) {
            monitorias[indice] = monitorias[indice].copy(
                monitorId = monitorId,
                disciplina = disciplina.trim(),
                curso = curso,
                diaSemana = diaSemana,
                horario = horario.trim(),
                sala = sala.trim()
            )
        }
        _scope?.launch(Dispatchers.IO) {
            _repository?.atualizarMonitoria(id, monitorId, disciplina, curso, diaSemana, horario, sala)
        }
    }

    fun responsaveisDoCurso(curso: String): List<Usuario> =
        usuarios.filter {
            it.curso == curso && (it.cargo == Cargo.MONITOR || it.cargo == Cargo.PROFESSOR)
        }

    // ------------------------------------------------------------------
    // Dúvidas
    // ------------------------------------------------------------------

    fun duvidasDoCurso(curso: String): List<Duvida> =
        duvidas.filter { it.curso == curso }.sortedByDescending { it.criadaEm }

    fun criarDuvida(
        autorId: Long,
        curso: String,
        disciplina: String,
        titulo: String,
        descricao: String
    ): Duvida {
        val nova = Duvida(
            id = proximoIdDuvida++,
            autorId = autorId,
            curso = curso,
            disciplina = disciplina.trim(),
            titulo = titulo.trim(),
            descricao = descricao.trim(),
            criadaEm = System.currentTimeMillis()
        )
        duvidas.add(nova)
        _scope?.launch(Dispatchers.IO) {
            _repository?.inserirDuvida(nova.toEntity())
        }
        return nova
    }

    fun buscarDuvida(id: Long): Duvida? = duvidas.firstOrNull { it.id == id }

    fun deletarDuvida(id: Long) {
        duvidas.removeAll { it.id == id }
        _scope?.launch(Dispatchers.IO) {
            _repository?.deletarDuvida(id)
        }
    }

    fun atualizarDuvida(id: Long, titulo: String, disciplina: String, descricao: String) {
        val indice = duvidas.indexOfFirst { it.id == id }
        if (indice >= 0) {
            val atual = duvidas[indice]
            duvidas[indice] = atual.copy(titulo = titulo.trim(), disciplina = disciplina.trim(), descricao = descricao.trim())
        }
        _scope?.launch(Dispatchers.IO) {
            _repository?.atualizarDuvida(id, titulo, disciplina, descricao)
        }
    }

    fun deletarResposta(duvidaId: Long, respostaId: Long) {
        val indice = duvidas.indexOfFirst { it.id == duvidaId }
        if (indice >= 0) {
            val duvida = duvidas[indice]
            duvida.respostas.removeAll { it.id == respostaId }
            duvidas[indice] = duvida.copy()
        }
        _scope?.launch(Dispatchers.IO) {
            _repository?.deletarResposta(respostaId)
        }
    }

    fun atualizarResposta(duvidaId: Long, respostaId: Long, texto: String) {
        val indice = duvidas.indexOfFirst { it.id == duvidaId }
        if (indice >= 0) {
            val duvida = duvidas[indice]
            val ri = duvida.respostas.indexOfFirst { it.id == respostaId }
            if (ri >= 0) {
                duvida.respostas[ri] = duvida.respostas[ri].copy(texto = texto.trim())
                duvidas[indice] = duvida.copy()
            }
        }
        _scope?.launch(Dispatchers.IO) {
            _repository?.atualizarResposta(respostaId, texto)
        }
    }

    fun responderDuvida(duvidaId: Long, autor: Usuario, texto: String): Boolean {
        val indice = duvidas.indexOfFirst { it.id == duvidaId }
        if (indice < 0) return false
        val duvida = duvidas[indice]
        if (duvida.curso != autor.curso) return false
        val podeResponder = autor.cargo == Cargo.MONITOR ||
                            autor.cargo == Cargo.PROFESSOR ||
                            autor.id == duvida.autorId
        if (!podeResponder) return false

        val nova = Resposta(
            id = proximoIdResposta++,
            autorId = autor.id,
            texto = texto.trim(),
            criadaEm = System.currentTimeMillis()
        )
        duvida.respostas.add(nova)
        duvidas[indice] = duvida.copy()

        _scope?.launch(Dispatchers.IO) {
            _repository?.inserirResposta(nova.toEntity(duvidaId))
        }
        return true
    }
}
