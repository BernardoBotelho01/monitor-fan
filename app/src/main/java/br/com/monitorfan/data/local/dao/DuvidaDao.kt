package br.com.monitorfan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.monitorfan.data.local.entity.DuvidaComRespostas
import br.com.monitorfan.data.local.entity.DuvidaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DuvidaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(duvida: DuvidaEntity): Long

    @Transaction
    @Query("SELECT * FROM duvidas ORDER BY criadaEm DESC")
    fun observarTodasComRespostas(): Flow<List<DuvidaComRespostas>>

    @Transaction
    @Query("SELECT * FROM duvidas WHERE curso = :curso ORDER BY criadaEm DESC")
    fun observarPorCursoComRespostas(curso: String): Flow<List<DuvidaComRespostas>>

    @Transaction
    @Query("SELECT * FROM duvidas WHERE id = :id LIMIT 1")
    suspend fun buscarComRespostas(id: Long): DuvidaComRespostas?

    @Query("DELETE FROM duvidas WHERE id = :id")
    suspend fun deletar(id: Long)

    @Query("UPDATE duvidas SET titulo = :titulo, disciplina = :disciplina, descricao = :descricao WHERE id = :id")
    suspend fun atualizarConteudo(id: Long, titulo: String, disciplina: String, descricao: String)

    @Query("SELECT COUNT(*) FROM duvidas")
    suspend fun contar(): Int
}
