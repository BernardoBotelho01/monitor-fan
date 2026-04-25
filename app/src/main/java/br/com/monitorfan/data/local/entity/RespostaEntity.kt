package br.com.monitorfan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.monitorfan.dados.Resposta

@Entity(tableName = "respostas")
data class RespostaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val duvidaId: Long,
    val autorId: Long,
    val texto: String,
    val criadaEm: Long
)

fun RespostaEntity.toDomain(): Resposta = Resposta(
    id = id,
    autorId = autorId,
    texto = texto,
    criadaEm = criadaEm
)

fun Resposta.toEntity(duvidaId: Long): RespostaEntity = RespostaEntity(
    id = id,
    duvidaId = duvidaId,
    autorId = autorId,
    texto = texto,
    criadaEm = criadaEm
)
