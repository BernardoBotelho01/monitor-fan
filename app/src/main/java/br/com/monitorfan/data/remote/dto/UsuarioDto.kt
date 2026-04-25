package br.com.monitorfan.data.remote.dto

import br.com.monitorfan.data.local.entity.UsuarioEntity

data class UsuarioDto(
    val id: Long,
    val nome: String,
    val email: String,
    val curso: String,
    val matricula: String,
    val cargo: String
)

fun UsuarioDto.toEntity(): UsuarioEntity = UsuarioEntity(
    id = id,
    nome = nome,
    email = email,
    senha = "",  // senha não trafega pela API
    curso = curso,
    matricula = matricula,
    cargo = cargo
)
