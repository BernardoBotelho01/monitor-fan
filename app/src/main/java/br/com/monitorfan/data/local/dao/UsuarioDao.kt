package br.com.monitorfan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.monitorfan.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(usuario: UsuarioEntity): Long

    @Update
    suspend fun atualizar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios ORDER BY nome ASC")
    fun observarTodos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios ORDER BY nome ASC")
    suspend fun listarTodos(): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE curso = :curso AND cargo != 'ADMIN' ORDER BY nome ASC")
    fun observarPorCurso(curso: String): Flow<List<UsuarioEntity>>

    @Query("UPDATE usuarios SET cargo = :cargo WHERE id = :id")
    suspend fun atualizarCargo(id: Long, cargo: String)

    @Query("UPDATE usuarios SET nome = :nome, email = :email, matricula = :matricula, curso = :curso, fotoUri = :fotoUri WHERE id = :id")
    suspend fun atualizarPerfil(id: Long, nome: String, email: String, matricula: String, curso: String, fotoUri: String?)

    @Query("SELECT * FROM usuarios WHERE LOWER(email) = LOWER(:email) AND matricula = :matricula LIMIT 1")
    suspend fun buscarPorEmailEMatricula(email: String, matricula: String): UsuarioEntity?

    @Query("UPDATE usuarios SET senha = :novaSenha WHERE id = :id")
    suspend fun atualizarSenha(id: Long, novaSenha: String)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios WHERE curso = :curso AND (cargo = 'MONITOR' OR cargo = 'PROFESSOR') ORDER BY nome ASC")
    suspend fun listarResponsaveisDoCurso(curso: String): List<UsuarioEntity>
}
