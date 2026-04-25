package br.com.monitorfan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.monitorfan.data.local.entity.RespostaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RespostaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(resposta: RespostaEntity): Long

    @Query("SELECT * FROM respostas WHERE duvidaId = :duvidaId ORDER BY criadaEm ASC")
    fun observarPorDuvida(duvidaId: Long): Flow<List<RespostaEntity>>

    @Query("SELECT * FROM respostas WHERE duvidaId = :duvidaId ORDER BY criadaEm ASC")
    suspend fun listarPorDuvida(duvidaId: Long): List<RespostaEntity>

    @Query("DELETE FROM respostas WHERE duvidaId = :duvidaId")
    suspend fun deletarPorDuvida(duvidaId: Long)

    @Query("DELETE FROM respostas WHERE id = :id")
    suspend fun deletar(id: Long)

    @Query("UPDATE respostas SET texto = :texto WHERE id = :id")
    suspend fun atualizarTexto(id: Long, texto: String)
}
