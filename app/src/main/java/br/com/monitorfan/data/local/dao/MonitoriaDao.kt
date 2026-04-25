package br.com.monitorfan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.monitorfan.data.local.entity.MonitoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(monitoria: MonitoriaEntity): Long

    @Query("SELECT * FROM monitorias ORDER BY curso ASC, disciplina ASC")
    fun observarTodas(): Flow<List<MonitoriaEntity>>

    @Query("SELECT * FROM monitorias WHERE curso = :curso ORDER BY disciplina ASC")
    fun observarPorCurso(curso: String): Flow<List<MonitoriaEntity>>

    @Query("SELECT * FROM monitorias ORDER BY curso ASC, disciplina ASC")
    suspend fun listarTodas(): List<MonitoriaEntity>

    @Query("DELETE FROM monitorias WHERE id = :id")
    suspend fun remover(id: Long)

    @Query("UPDATE monitorias SET monitorId = :monitorId, disciplina = :disciplina, curso = :curso, diaSemana = :diaSemana, horario = :horario, sala = :sala WHERE id = :id")
    suspend fun atualizar(id: Long, monitorId: Long, disciplina: String, curso: String, diaSemana: String, horario: String, sala: String)

    @Query("SELECT COUNT(*) FROM monitorias")
    suspend fun contar(): Int
}
