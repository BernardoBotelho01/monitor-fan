package br.com.monitorfan.data.firebase

import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Duvida
import br.com.monitorfan.dados.Monitoria
import br.com.monitorfan.dados.Resposta
import br.com.monitorfan.dados.Usuario
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val colUsuarios = db.collection("usuarios")
    private val colMonitorias = db.collection("monitorias")
    private val colDuvidas = db.collection("duvidas")

    // ------------------------------------------------------------------
    // Autenticação
    // ------------------------------------------------------------------

    suspend fun login(email: String, senha: String): Usuario {
        val result = auth.signInWithEmailAndPassword(email.trim(), senha).await()
        val uid = result.user!!.uid
        return buscarUsuario(uid) ?: throw Exception("Perfil não encontrado")
    }

    suspend fun cadastrar(
        nome: String, email: String, senha: String, curso: String, matricula: String
    ): Usuario {
        val result = auth.createUserWithEmailAndPassword(email.trim(), senha).await()
        val uid = result.user!!.uid
        val usuario = Usuario(
            id = uid,
            nome = nome.trim(),
            email = email.trim().lowercase(),
            curso = curso,
            matricula = matricula.trim(),
            cargo = Cargo.USUARIO
        )
        colUsuarios.document(uid).set(usuario.toMap()).await()
        return usuario
    }

    fun logout() = auth.signOut()

    suspend fun enviarEmailRedefinicaoSenha(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    suspend fun alterarSenha(senhaAtual: String, novaSenha: String) {
        val user = auth.currentUser ?: throw Exception("Não autenticado")
        val credential = EmailAuthProvider.getCredential(user.email!!, senhaAtual)
        user.reauthenticate(credential).await()
        user.updatePassword(novaSenha).await()
    }

    suspend fun deletarConta(senha: String): Boolean {
        val user = auth.currentUser ?: return false
        val credential = EmailAuthProvider.getCredential(user.email!!, senha)
        user.reauthenticate(credential).await()

        val uid = user.uid
        val duvidasDocs = colDuvidas.whereEqualTo("autorId", uid).get().await()
        for (doc in duvidasDocs) {
            val respostas = colDuvidas.document(doc.id).collection("respostas").get().await()
            respostas.forEach { it.reference.delete().await() }
            doc.reference.delete().await()
        }
        colMonitorias.whereEqualTo("monitorId", uid).get().await()
            .forEach { it.reference.delete().await() }
        colUsuarios.document(uid).delete().await()
        user.delete().await()
        return true
    }

    // ------------------------------------------------------------------
    // Usuários
    // ------------------------------------------------------------------

    suspend fun buscarUsuario(uid: String): Usuario? =
        colUsuarios.document(uid).get().await().toUsuario()

    suspend fun emailJaCadastrado(email: String): Boolean =
        colUsuarios.whereEqualTo("email", email.trim().lowercase()).get().await().isEmpty.not()

    fun observarTodosUsuarios(): Flow<List<Usuario>> = callbackFlow {
        val listener = colUsuarios.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.documents?.mapNotNull { it.toUsuario() } ?: emptyList())
        }
        awaitClose { listener.remove() }
    }

    suspend fun atualizarCargo(uid: String, cargo: Cargo) {
        colUsuarios.document(uid).update("cargo", cargo.name).await()
    }

    suspend fun atualizarPerfil(usuario: Usuario) {
        colUsuarios.document(usuario.id).update(
            mapOf(
                "nome" to usuario.nome.trim(),
                "matricula" to usuario.matricula.trim(),
                "curso" to usuario.curso,
                "fotoUri" to usuario.fotoUri
            )
        ).await()
    }

    suspend fun solicitarTrocaEmail(novoEmail: String) {
        val user = auth.currentUser ?: throw Exception("Não autenticado")
        user.verifyBeforeUpdateEmail(novoEmail.trim().lowercase()).await()
    }

    suspend fun sincronizarEmailComAuth(uid: String, emailAuth: String) {
        colUsuarios.document(uid).update("email", emailAuth.trim().lowercase()).await()
    }

    // ------------------------------------------------------------------
    // Monitorias
    // ------------------------------------------------------------------

    fun observarTodasMonitorias(): Flow<List<Monitoria>> = callbackFlow {
        val listener = colMonitorias.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.documents?.mapNotNull { it.toMonitoria() } ?: emptyList())
        }
        awaitClose { listener.remove() }
    }

    suspend fun inserirMonitoria(monitoria: Monitoria): String {
        val ref = colMonitorias.add(monitoria.toMap()).await()
        return ref.id
    }

    suspend fun removerMonitoria(id: String) {
        colMonitorias.document(id).delete().await()
    }

    suspend fun atualizarMonitoria(
        id: String, monitorId: String, disciplina: String,
        curso: String, diaSemana: String, horario: String, sala: String
    ) {
        colMonitorias.document(id).update(
            mapOf(
                "monitorId" to monitorId,
                "disciplina" to disciplina.trim(),
                "curso" to curso,
                "diaSemana" to diaSemana,
                "horario" to horario.trim(),
                "sala" to sala.trim()
            )
        ).await()
    }

    // ------------------------------------------------------------------
    // Dúvidas
    // ------------------------------------------------------------------

    fun observarTodasDuvidas(): Flow<List<Duvida>> = callbackFlow {
        val listener = colDuvidas.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.documents?.mapNotNull { it.toDuvida() } ?: emptyList())
        }
        awaitClose { listener.remove() }
    }

    suspend fun inserirDuvida(duvida: Duvida): String {
        val ref = colDuvidas.add(duvida.toMap()).await()
        return ref.id
    }

    suspend fun deletarDuvida(id: String) {
        val respostas = colDuvidas.document(id).collection("respostas").get().await()
        respostas.forEach { it.reference.delete().await() }
        colDuvidas.document(id).delete().await()
    }

    suspend fun atualizarDuvida(id: String, titulo: String, disciplina: String, descricao: String) {
        colDuvidas.document(id).update(
            mapOf(
                "titulo" to titulo.trim(),
                "disciplina" to disciplina.trim(),
                "descricao" to descricao.trim()
            )
        ).await()
    }

    // ------------------------------------------------------------------
    // Respostas
    // ------------------------------------------------------------------

    fun observarRespostasDaDuvida(duvidaId: String): Flow<List<Resposta>> = callbackFlow {
        val listener = colDuvidas.document(duvidaId).collection("respostas")
            .orderBy("criadaEm")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.mapNotNull { it.toResposta() } ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun inserirResposta(duvidaId: String, resposta: Resposta, autorCargo: Cargo): String {
        val ref = colDuvidas.document(duvidaId).collection("respostas").add(resposta.toMap()).await()
        val updates = mutableMapOf<String, Any>("respostasCount" to FieldValue.increment(1))
        if (autorCargo == Cargo.MONITOR || autorCargo == Cargo.PROFESSOR) {
            updates["respondidaPorMonitor"] = true
        }
        colDuvidas.document(duvidaId).update(updates).await()
        return ref.id
    }

    suspend fun deletarResposta(duvidaId: String, respostaId: String) {
        colDuvidas.document(duvidaId).collection("respostas").document(respostaId).delete().await()
        colDuvidas.document(duvidaId).update("respostasCount", FieldValue.increment(-1)).await()
    }

    suspend fun atualizarResposta(duvidaId: String, respostaId: String, texto: String) {
        colDuvidas.document(duvidaId).collection("respostas").document(respostaId)
            .update("texto", texto.trim()).await()
    }

    // ------------------------------------------------------------------
    // Helpers de conversão
    // ------------------------------------------------------------------

    private fun Usuario.toMap() = mapOf(
        "nome" to nome,
        "email" to email,
        "curso" to curso,
        "matricula" to matricula,
        "cargo" to cargo.name,
        "fotoUri" to fotoUri
    )

    private fun Monitoria.toMap() = mapOf(
        "monitorId" to monitorId,
        "disciplina" to disciplina,
        "curso" to curso,
        "diaSemana" to diaSemana,
        "horario" to horario,
        "sala" to sala
    )

    private fun formatarData(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).also {
            it.timeZone = java.util.TimeZone.getTimeZone("America/Sao_Paulo")
        }.format(Date(millis))

    private fun parsearData(valor: String): Long =
        runCatching {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).also {
                it.timeZone = java.util.TimeZone.getTimeZone("America/Sao_Paulo")
            }.parse(valor)?.time ?: 0L
        }.getOrDefault(0L)

    private fun Duvida.toMap() = mapOf(
        "autorId" to autorId,
        "curso" to curso,
        "disciplina" to disciplina,
        "titulo" to titulo,
        "descricao" to descricao,
        "criadaEm" to formatarData(criadaEm),
        "respostasCount" to respostasCount,
        "respondidaPorMonitor" to respondidaPorMonitor
    )

    private fun Resposta.toMap() = mapOf(
        "autorId" to autorId,
        "texto" to texto,
        "criadaEm" to formatarData(criadaEm)
    )

    private fun DocumentSnapshot.toUsuario(): Usuario? {
        val cargo = runCatching {
            Cargo.valueOf((getString("cargo") ?: "").uppercase())
        }.getOrDefault(Cargo.USUARIO)
        return Usuario(
            id = id,
            nome = getString("nome") ?: return null,
            email = getString("email") ?: return null,
            curso = getString("curso") ?: return null,
            matricula = getString("matricula") ?: return null,
            cargo = cargo,
            fotoUri = getString("fotoUri")
        )
    }

    private fun DocumentSnapshot.toMonitoria(): Monitoria? {
        return Monitoria(
            id = id,
            monitorId = getString("monitorId") ?: return null,
            disciplina = getString("disciplina") ?: return null,
            curso = getString("curso") ?: return null,
            diaSemana = getString("diaSemana") ?: return null,
            horario = getString("horario") ?: return null,
            sala = getString("sala") ?: return null
        )
    }

    private fun DocumentSnapshot.lerData(campo: String): Long =
        when (val v = get(campo)) {
            is Number -> v.toLong()
            is String -> parsearData(v)
            else -> 0L
        }

    private fun DocumentSnapshot.toDuvida(): Duvida? {
        return Duvida(
            id = id,
            autorId = getString("autorId") ?: return null,
            curso = getString("curso") ?: return null,
            disciplina = getString("disciplina") ?: return null,
            titulo = getString("titulo") ?: return null,
            descricao = getString("descricao") ?: return null,
            criadaEm = lerData("criadaEm"),
            respostasCount = getLong("respostasCount")?.toInt() ?: 0,
            respondidaPorMonitor = getBoolean("respondidaPorMonitor") ?: false
        )
    }

    private fun DocumentSnapshot.toResposta(): Resposta? {
        return Resposta(
            id = id,
            autorId = getString("autorId") ?: return null,
            texto = getString("texto") ?: return null,
            criadaEm = lerData("criadaEm")
        )
    }
}
