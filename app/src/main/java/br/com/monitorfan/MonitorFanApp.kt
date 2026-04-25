package br.com.monitorfan

import android.app.Application
import br.com.monitorfan.data.local.database.AppDatabase
import br.com.monitorfan.data.remote.RetrofitClient
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.util.SessaoPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MonitorFanApp : Application() {

    // Escopo de aplicação sobrevive a telas e configurações
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var repository: MonitorFanRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = MonitorFanRepository(
            usuarioDao = database.usuarioDao(),
            monitoriaDao = database.monitoriaDao(),
            duvidaDao = database.duvidaDao(),
            respostaDao = database.respostaDao(),
            apiService = RetrofitClient.apiService
        )
        appScope.launch(Dispatchers.IO) {
            repository.inicializar()
            SessaoPrefs.recuperar(this@MonitorFanApp)?.let { savedId ->
                val usuario = repository.buscarUsuario(savedId)
                if (usuario != null) {
                    withContext(Dispatchers.Main) { Repositorio.usuarioLogado.value = usuario }
                } else {
                    SessaoPrefs.limpar(this@MonitorFanApp)
                }
            }
            withContext(Dispatchers.Main) { Repositorio.isInicializado.value = true }
        }
        Repositorio.inicializar(appScope, repository)
    }
}
