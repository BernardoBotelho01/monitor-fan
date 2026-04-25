package br.com.monitorfan.dados

/**
 * Perfis disponíveis no sistema.
 * Todo cadastro nasce como USUARIO. O ADMIN é quem promove
 * alguém a MONITOR ou PROFESSOR.
 */
enum class Cargo(val rotulo: String) {
    USUARIO("Aluno"),
    MONITOR("Monitor"),
    PROFESSOR("Professor"),
    ADMIN("Administrador")
}

/**
 * Cursos disponíveis. Lista fixa para manter a separação de acesso
 * por curso consistente.
 */
object Cursos {
    val disponiveis: List<String> = listOf(
        "Administração",
        "Biomedicina",
        "Ciências Contábeis",
        "Direito",
        "Enfermagem",
        "Engenharia de Software",
        "Estética e Cosmética",
        "Farmácia",
        "Fisioterapia",
        "Medicina",
        "Odontologia",
        "Pedagogia",
        "Psicologia",
        "Segurança da Informação"
    )
}

data class Usuario(
    val id: Long,
    var nome: String,
    var email: String,
    var senha: String,
    var curso: String,
    var matricula: String,
    var cargo: Cargo,
    var fotoUri: String? = null
)

data class Monitoria(
    val id: Long,
    val monitorId: Long,
    val disciplina: String,
    val curso: String,
    val diaSemana: String,
    val horario: String,
    val sala: String
)

data class Resposta(
    val id: Long,
    val autorId: Long,
    val texto: String,
    val criadaEm: Long
)

data class Duvida(
    val id: Long,
    val autorId: Long,
    val curso: String,
    val disciplina: String,
    val titulo: String,
    val descricao: String,
    val criadaEm: Long,
    val respostas: MutableList<Resposta> = mutableListOf()
)
