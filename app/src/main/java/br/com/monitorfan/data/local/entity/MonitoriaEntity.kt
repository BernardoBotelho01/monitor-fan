package br.com.monitorfan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.monitorfan.dados.Monitoria

@Entity(tableName = "monitorias")
data class MonitoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monitorId: Long,
    val disciplina: String,
    val curso: String,
    val diaSemana: String,
    val horario: String,
    val sala: String
)

fun MonitoriaEntity.toDomain(): Monitoria = Monitoria(
    id = id,
    monitorId = monitorId,
    disciplina = disciplina,
    curso = curso,
    diaSemana = diaSemana,
    horario = horario,
    sala = sala
)

fun Monitoria.toEntity(): MonitoriaEntity = MonitoriaEntity(
    id = id,
    monitorId = monitorId,
    disciplina = disciplina,
    curso = curso,
    diaSemana = diaSemana,
    horario = horario,
    sala = sala
)
