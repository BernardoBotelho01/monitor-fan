package br.com.monitorfan

import android.app.Application
import br.com.monitorfan.data.local.database.AppDatabase
import br.com.monitorfan.data.remote.RetrofitClient
import br.com.monitorfan.data.repository.MonitorFanRepository
import br.com.monitorfan.dados.Repositorio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
            // Semeia o banco na primeira execução e tenta sincronizar com a API
            repository.inicializar()
        }
        // Registra o repositório no Repositorio legado para que as telas atuais
        // continuem funcionando enquanto a migração para ViewModels ocorre gradualmente
        Repositorio.inicializar(appScope, repository)
    }
}
