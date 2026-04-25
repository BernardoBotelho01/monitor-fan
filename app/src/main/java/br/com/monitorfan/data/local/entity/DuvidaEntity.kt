package br.com.monitorfan.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import br.com.monitorfan.dados.Duvida

@Entity(tableName = "duvidas")
data class DuvidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val autorId: Long,
    val curso: String,
    val disciplina: String,
    val titulo: String,
    val descricao: String,
    val criadaEm: Long
)

data class DuvidaComRespostas(
    @Embedded val duvida: DuvidaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "duvidaId"
    )
    val respostas: List<RespostaEntity>
)

fun DuvidaEntity.toDomain(): Duvida = Duvida(
    id = id,
    autorId = autorId,
    curso = curso,
    disciplina = disciplina,
    titulo = titulo,
    descricao = descricao,
    criadaEm = criadaEm
)

fun DuvidaComRespostas.toDomain(): Duvida = Duvida(
    id = duvida.id,
    autorId = duvida.autorId,
    curso = duvida.curso,
    disciplina = duvida.disciplina,
    titulo = duvida.titulo,
    descricao = duvida.descricao,
    criadaEm = duvida.criadaEm,
    respostas = respostas.map { it.toDomain() }.toMutableList()
)

fun Duvida.toEntity(): DuvidaEntity = DuvidaEntity(
    id = id,
    autorId = autorId,
    curso = curso,
    disciplina = disciplina,
    titulo = titulo,
    descricao = descricao,
    criadaEm = criadaEm
)
