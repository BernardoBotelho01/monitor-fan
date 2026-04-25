package br.com.monitorfan.data.repository

import br.com.monitorfan.data.local.dao.DuvidaDao
import br.com.monitorfan.data.local.dao.MonitoriaDao
import br.com.monitorfan.data.local.dao.RespostaDao
import br.com.monitorfan.data.local.dao.UsuarioDao
import br.com.monitorfan.data.local.entity.DuvidaComRespostas
import br.com.monitorfan.data.local.entity.DuvidaEntity
import br.com.monitorfan.data.local.entity.MonitoriaEntity
import br.com.monitorfan.data.local.entity.RespostaEntity
import br.com.monitorfan.data.local.entity.UsuarioEntity
import br.com.monitorfan.data.local.entity.toDomain
import br.com.monitorfan.data.remote.ApiService
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Duvida
import br.com.monitorfan.dados.Monitoria
import br.com.monitorfan.dados.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MonitorFanRepository(
    private val usuarioDao: UsuarioDao,
    private val monitoriaDao: MonitoriaDao,
    private val duvidaDao: DuvidaDao,
    private val respostaDao: RespostaDao,
    private val apiService: ApiService
) {

    // ------------------------------------------------------------------
    // Inicialização (seed de dados na primeira execução)
    // ------------------------------------------------------------------

    suspend fun inicializar() {
        if (usuarioDao.contar() == 0) {
            inserirDadosIniciais()
        }
        sincronizarComApi()
    }

    // ------------------------------------------------------------------
    // Autenticação
    // ------------------------------------------------------------------

    suspend fun autenticar(email: String, senha: String): Usuario? {
        return usuarioDao.buscarPorEmail(email.trim().lowercase())?.let { entity ->
            if (entity.senha == senha) entity.toDomain() else null
        }
    }

    suspend fun emailJaCadastrado(email: String): Boolean {
        return usuarioDao.buscarPorEmail(email.trim().lowercase()) != null
    }

    // ------------------------------------------------------------------
    // Usuários
    // ------------------------------------------------------------------

    fun observarTodosUsuarios(): Flow<List<Usuario>> =
        usuarioDao.observarTodos().map { lista -> lista.map { it.toDomain() } }

    suspend fun inserirUsuario(entity: UsuarioEntity): Long =
        usuarioDao.inserir(entity)

    suspend fun atualizarCargo(usuarioId: Long, cargo: Cargo) {
        usuarioDao.atualizarCargo(usuarioId, cargo.name)
    }

    suspend fun atualizarPerfil(usuario: Usuario) {
        usuarioDao.atualizarPerfil(
            id = usuario.id,
            nome = usuario.nome.trim(),
            email = usuario.email.trim().lowercase(),
            matricula = usuario.matricula.trim(),
            curso = usuario.curso,
            fotoUri = usuario.fotoUri
        )
    }

    suspend fun alterarSenha(usuarioId: Long, senhaAtual: String, novaSenha: String): Boolean {
        val usuario = usuarioDao.buscarPorId(usuarioId)
        return if (usuario != null && usuario.senha == senhaAtual) {
            usuarioDao.atualizarSenha(usuarioId, novaSenha)
            true
        } else false
    }

    suspend fun redefinirSenha(email: String, matricula: String, novaSenha: String): Boolean {
        val usuario = usuarioDao.buscarPorEmailEMatricula(email.trim().lowercase(), matricula.trim())
        return if (usuario != null) {
            usuarioDao.atualizarSenha(usuario.id, novaSenha)
            true
        } else false
    }

    suspend fun emailJaCadastradoPorOutro(email: String, excluindoId: Long): Boolean {
        val existente = usuarioDao.buscarPorEmail(email.trim().lowercase())
        return existente != null && existente.id != excluindoId
    }

    suspend fun buscarUsuario(id: Long): Usuario? =
        usuarioDao.buscarPorId(id)?.toDomain()

    suspend fun listarResponsaveisDoCurso(curso: String): List<Usuario> =
        usuarioDao.listarResponsaveisDoCurso(curso).map { it.toDomain() }

    // ------------------------------------------------------------------
    // Monitorias
    // ------------------------------------------------------------------

    fun observarTodasMonitorias(): Flow<List<Monitoria>> =
        monitoriaDao.observarTodas().map { lista -> lista.map { it.toDomain() } }

    fun observarMonitoriasDoCurso(curso: String): Flow<List<Monitoria>> =
        monitoriaDao.observarPorCurso(curso).map { lista -> lista.map { it.toDomain() } }

    suspend fun inserirMonitoria(entity: MonitoriaEntity): Long =
        monitoriaDao.inserir(entity)

    suspend fun removerMonitoria(id: Long) =
        monitoriaDao.remover(id)

    suspend fun atualizarMonitoria(
        id: Long, monitorId: Long, disciplina: String,
        curso: String, diaSemana: String, horario: String, sala: String
    ) = monitoriaDao.atualizar(id, monitorId, disciplina.trim(), curso, diaSemana, horario.trim(), sala.trim())

    // ------------------------------------------------------------------
    // Dúvidas
    // ------------------------------------------------------------------

    fun observarTodasDuvidasComRespostas(): Flow<List<DuvidaComRespostas>> =
        duvidaDao.observarTodasComRespostas()

    fun observarDuvidasDoCurso(curso: String): Flow<List<Duvida>> =
        duvidaDao.observarPorCursoComRespostas(curso).map { lista -> lista.map { it.toDomain() } }

    suspend fun inserirDuvida(entity: DuvidaEntity): Long =
        duvidaDao.inserir(entity)

    suspend fun deletarDuvida(id: Long) {
        respostaDao.deletarPorDuvida(id)
        duvidaDao.deletar(id)
    }

    suspend fun atualizarDuvida(id: Long, titulo: String, disciplina: String, descricao: String) {
        duvidaDao.atualizarConteudo(id, titulo.trim(), disciplina.trim(), descricao.trim())
    }

    suspend fun buscarDuvida(id: Long): Duvida? =
        duvidaDao.buscarComRespostas(id)?.toDomain()

    // ------------------------------------------------------------------
    // Respostas
    // ------------------------------------------------------------------

    suspend fun inserirResposta(entity: RespostaEntity): Long =
        respostaDao.inserir(entity)

    suspend fun deletarResposta(respostaId: Long) =
        respostaDao.deletar(respostaId)

    suspend fun atualizarResposta(respostaId: Long, texto: String) =
        respostaDao.atualizarTexto(respostaId, texto.trim())

    // ------------------------------------------------------------------
    // Sincronização offline-first com API
    // ------------------------------------------------------------------

    private suspend fun sincronizarComApi() {
        try {
            val respUsuarios = apiService.buscarUsuarios()
            if (respUsuarios.isSuccessful) {
                respUsuarios.body()?.forEach { dto ->
                    // Só insere/atualiza usuários do servidor, sem sobrescrever senhas locais
                    val local = usuarioDao.buscarPorEmail(dto.email)
                    if (local == null) {
                        usuarioDao.inserir(dto.toEntity())
                    }
                }
            }
        } catch (_: Exception) {
            // Sem internet ou backend indisponível: usa dados locais do Room
        }

        try {
            val respMonitorias = apiService.buscarMonitorias()
            if (respMonitorias.isSuccessful) {
                respMonitorias.body()?.forEach { dto ->
                    monitoriaDao.inserir(dto.toEntity())
                }
            }
        } catch (_: Exception) { }

        try {
            val respDuvidas = apiService.buscarDuvidas()
            if (respDuvidas.isSuccessful) {
                respDuvidas.body()?.forEach { dto ->
                    duvidaDao.inserir(dto.toEntity())
                }
            }
        } catch (_: Exception) { }
    }

    // ------------------------------------------------------------------
    // Dados iniciais (seed na primeira execução)
    // ------------------------------------------------------------------

    private suspend fun inserirDadosIniciais() {
        val adminId = usuarioDao.inserir(
            UsuarioEntity(
                nome = "Administrador",
                email = "admin@monitorfan.com",
                senha = "admin123",
                curso = "Engenharia de Software",
                matricula = "ADM0001",
                cargo = Cargo.ADMIN.name
            )
        )

        val anaId = usuarioDao.inserir(
            UsuarioEntity(
                nome = "Ana Martins",
                email = "ana@monitorfan.com",
                senha = "123456",
                curso = "Engenharia de Software",
                matricula = "ES2023001",
                cargo = Cargo.MONITOR.name
            )
        )

        val rafaelId = usuarioDao.inserir(
            UsuarioEntity(
                nome = "Rafael Oliveira",
                email = "rafael@monitorfan.com",
                senha = "123456",
                curso = "Engenharia de Software",
                matricula = "ES2023002",
                cargo = Cargo.PROFESSOR.name
            )
        )

        usuarioDao.inserir(
            UsuarioEntity(
                nome = "Lucas Andrade",
                email = "lucas@monitorfan.com",
                senha = "123456",
                curso = "Engenharia de Software",
                matricula = "ES2024010",
                cargo = Cargo.USUARIO.name
            )
        )

        usuarioDao.inserir(
            UsuarioEntity(
                nome = "Marina Souza",
                email = "marina@monitorfan.com",
                senha = "123456",
                curso = "Psicologia",
                matricula = "PS2024005",
                cargo = Cargo.USUARIO.name
            )
        )

        val pedroId = usuarioDao.inserir(
            UsuarioEntity(
                nome = "Pedro Lima",
                email = "pedro@monitorfan.com",
                senha = "123456",
                curso = "Psicologia",
                matricula = "PS2023009",
                cargo = Cargo.MONITOR.name
            )
        )

        monitoriaDao.inserir(
            MonitoriaEntity(
                monitorId = anaId,
                disciplina = "Cálculo I",
                curso = "Engenharia de Software",
                diaSemana = "Segunda",
                horario = "14:00 - 16:00",
                sala = "Lab 03"
            )
        )

        monitoriaDao.inserir(
            MonitoriaEntity(
                monitorId = rafaelId,
                disciplina = "Estrutura de Dados",
                curso = "Engenharia de Software",
                diaSemana = "Quarta",
                horario = "10:00 - 12:00",
                sala = "Sala 204"
            )
        )

        monitoriaDao.inserir(
            MonitoriaEntity(
                monitorId = pedroId,
                disciplina = "Psicologia Cognitiva",
                curso = "Psicologia",
                diaSemana = "Terça",
                horario = "16:00 - 18:00",
                sala = "Sala 110"
            )
        )
    }
}

// Extension para converter DTO (evita import circular)
private fun br.com.monitorfan.data.remote.dto.UsuarioDto.toEntity() =
    br.com.monitorfan.data.local.entity.UsuarioEntity(
        id = id, nome = nome, email = email, senha = "", curso = curso,
        matricula = matricula, cargo = cargo
    )

private fun br.com.monitorfan.data.remote.dto.MonitoriaDto.toEntity() =
    br.com.monitorfan.data.local.entity.MonitoriaEntity(
        id = id, monitorId = monitorId, disciplina = disciplina, curso = curso,
        diaSemana = diaSemana, horario = horario, sala = sala
    )

private fun br.com.monitorfan.data.remote.dto.DuvidaDto.toEntity() =
    br.com.monitorfan.data.local.entity.DuvidaEntity(
        id = id, autorId = autorId, curso = curso, disciplina = disciplina,
        titulo = titulo, descricao = descricao, criadaEm = criadaEm
    )
