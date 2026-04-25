package br.com.monitorfan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.navegacao.AppMonitorFanAdmin
import br.com.monitorfan.navegacao.AppMonitorFanLogado
import br.com.monitorfan.ui.telas.TelaCadastro
import br.com.monitorfan.ui.telas.TelaEsqueceuSenha
import br.com.monitorfan.ui.telas.TelaLogin
import br.com.monitorfan.ui.telas.TelaSplash
import br.com.monitorfan.ui.theme.MonitorFanTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonitorFanTheme {
                MonitorFan()
            }
        }
    }
}

@Composable
fun MonitorFan() {
    var splashVisivel by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Aguarda o banco inicializar (máx. 5 segundos por segurança)
        kotlinx.coroutines.withTimeoutOrNull(5_000) {
            snapshotFlow { Repositorio.isInicializado.value }
                .filter { it }
                .first()
        }
        delay(700) // tempo mínimo para o splash ser visto
        splashVisivel = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ConteudoPrincipal()

        AnimatedVisibility(
            visible = splashVisivel,
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            TelaSplash()
        }
    }
}

@Composable
private fun ConteudoPrincipal() {
    val usuarioLogado by Repositorio.usuarioLogado
    var telaAtual by remember { mutableStateOf("login") }

    when {
        usuarioLogado == null && telaAtual == "login" -> {
            TelaLogin(
                onLoginSucesso = {},
                onCriarContaClick = { telaAtual = "cadastro" },
                onEsqueceuSenhaClick = { telaAtual = "esqueceu_senha" }
            )
        }

        usuarioLogado == null && telaAtual == "esqueceu_senha" -> {
            TelaEsqueceuSenha(
                onVoltar = { telaAtual = "login" }
            )
        }

        usuarioLogado == null && telaAtual == "cadastro" -> {
            TelaCadastro(
                onCadastrarClick = { telaAtual = "login" },
                onVoltarLoginClick = { telaAtual = "login" }
            )
        }

        usuarioLogado != null -> {
            if (usuarioLogado?.cargo == Cargo.ADMIN) {
                AppMonitorFanAdmin(onLogout = { telaAtual = "login" })
            } else {
                AppMonitorFanLogado(onLogout = { telaAtual = "login" })
            }
        }
    }
}
