package br.com.monitorfan.data.remote.dto

import br.com.monitorfan.data.local.entity.RespostaEntity

data class RespostaDto(
    val id: Long,
    val duvidaId: Long,
    val autorId: Long,
    val texto: String,
    val criadaEm: Long
)

fun RespostaDto.toEntity(): RespostaEntity = RespostaEntity(
    id = id,
    duvidaId = duvidaId,
    autorId = autorId,
    texto = texto,
    criadaEm = criadaEm
)
