package br.com.monitorfan.dados

enum class Cargo(val rotulo: String) {
    USUARIO("Aluno"),
    MONITOR("Monitor"),
    PROFESSOR("Professor"),
    ADMIN("Administrador")
}

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
    val id: String = "",
    var nome: String,
    var email: String,
    var curso: String,
    var matricula: String,
    var cargo: Cargo,
    var fotoUri: String? = null
)

data class Monitoria(
    val id: String = "",
    val monitorId: String,
    val disciplina: String,
    val curso: String,
    val diaSemana: String,
    val horario: String,
    val sala: String
)

data class Resposta(
    val id: String = "",
    val autorId: String,
    val texto: String,
    val criadaEm: Long
)

data class Duvida(
    val id: String = "",
    val autorId: String,
    val curso: String,
    val disciplina: String,
    val titulo: String,
    val descricao: String,
    val criadaEm: Long,
    val respostasCount: Int = 0,
    val respondidaPorMonitor: Boolean = false,
    val respostas: MutableList<Resposta> = mutableListOf()
)
