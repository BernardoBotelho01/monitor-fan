package br.com.monitorfan

import android.app.Application
import br.com.monitorfan.data.firebase.FirebaseRepository
import br.com.monitorfan.dados.Repositorio
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MonitorFanApp : Application() {

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var firebaseRepository: FirebaseRepository
        private set

    override fun onCreate() {
        super.onCreate()
        firebaseRepository = FirebaseRepository()
        Repositorio.inicializar(appScope, firebaseRepository)

        appScope.launch(Dispatchers.IO) {
            try {
                Firebase.auth.currentUser?.reload()?.await()
                Firebase.auth.currentUser?.uid?.let { uid ->
                    val usuario = firebaseRepository.buscarUsuario(uid)
                    val emailAuth = Firebase.auth.currentUser?.email?.trim()?.lowercase()
                    if (usuario != null && emailAuth != null && emailAuth != usuario.email.lowercase()) {
                        firebaseRepository.sincronizarEmailComAuth(uid, emailAuth)
                    }
                    withContext(Dispatchers.Main) {
                        Repositorio.usuarioLogado.value = usuario?.copy(
                            email = emailAuth ?: usuario.email
                        )
                    }
                }
            } catch (_: Exception) {
            } finally {
                withContext(Dispatchers.Main) {
                    Repositorio.isInicializado.value = true
                }
            }
        }

    }
}
