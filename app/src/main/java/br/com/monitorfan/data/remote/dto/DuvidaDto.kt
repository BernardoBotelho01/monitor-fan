package br.com.monitorfan.data.remote.dto

import br.com.monitorfan.data.local.entity.DuvidaEntity

data class DuvidaDto(
    val id: Long,
    val autorId: Long,
    val curso: String,
    val disciplina: String,
    val titulo: String,
    val descricao: String,
    val criadaEm: Long
)

fun DuvidaDto.toEntity(): DuvidaEntity = DuvidaEntity(
    id = id,
    autorId = autorId,
    curso = curso,
    disciplina = disciplina,
    titulo = titulo,
    descricao = descricao,
    criadaEm = criadaEm
)
