package br.com.monitorfan.data.remote.dto

import br.com.monitorfan.data.local.entity.MonitoriaEntity

data class MonitoriaDto(
    val id: Long,
    val monitorId: Long,
    val disciplina: String,
    val curso: String,
    val diaSemana: String,
    val horario: String,
    val sala: String
)

fun MonitoriaDto.toEntity(): MonitoriaEntity = MonitoriaEntity(
    id = id,
    monitorId = monitorId,
    disciplina = disciplina,
    curso = curso,
    diaSemana = diaSemana,
    horario = horario,
    sala = sala
)
