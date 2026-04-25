package br.com.monitorfan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Usuario

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    // AVISO: Em produção, NUNCA armazene senha em texto puro.
    // Use autenticação via token (JWT) com backend real e HTTPS.
    val senha: String,
    val curso: String,
    val matricula: String,
    val cargo: String,
    val fotoUri: String? = null
)

fun UsuarioEntity.toDomain(): Usuario = Usuario(
    id = id,
    nome = nome,
    email = email,
    senha = senha,
    curso = curso,
    matricula = matricula,
    cargo = Cargo.valueOf(cargo),
    fotoUri = fotoUri
)

fun Usuario.toEntity(): UsuarioEntity = UsuarioEntity(
    id = id,
    nome = nome,
    email = email,
    senha = senha,
    curso = curso,
    matricula = matricula,
    cargo = cargo.name,
    fotoUri = fotoUri
)
