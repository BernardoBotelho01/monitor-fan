package br.com.monitorfan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.com.monitorfan.navegacao.AppMonitorFanLogado
import com.example.appexemplo.ui.telas.CadastroScreen
import com.example.appexemplo.ui.telas.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonitorFan()
        }
    }
}

@Composable
fun MonitorFan() {
    var telaAtual by remember { mutableStateOf("login") }
    var usuarioLogado by remember { mutableStateOf(false) }

    when {
        !usuarioLogado && telaAtual == "login" -> {
            LoginScreen(
                onEntrarClick = {
                    usuarioLogado = true
                },
                onCriarContaClick = {
                    telaAtual = "cadastro"
                }
            )
        }

        !usuarioLogado && telaAtual == "cadastro" -> {
            CadastroScreen(
                onCadastrarClick = {
                    telaAtual = "login"
                },
                onVoltarLoginClick = {
                    telaAtual = "login"
                }
            )
        }

        usuarioLogado -> {
            AppMonitorFanLogado(
                onLogout = {
                    usuarioLogado = false
                    telaAtual = "login"
                }
            )
        }
    }
}
