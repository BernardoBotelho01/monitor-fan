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
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.navegacao.AppMonitorFanAdmin
import br.com.monitorfan.navegacao.AppMonitorFanLogado
import br.com.monitorfan.ui.telas.TelaCadastro
import br.com.monitorfan.ui.telas.TelaEsqueceuSenha
import br.com.monitorfan.ui.telas.TelaLogin
import br.com.monitorfan.ui.theme.MonitorFanTheme

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
    // Fonte de verdade do usuário logado: o repositório.
    val usuarioLogado by Repositorio.usuarioLogado

    // Só controla a navegação entre login e cadastro enquanto ninguém está logado.
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
                onCadastrarClick = {
                    // Depois de cadastrar, volta para o login —
                    // a promoção para monitor/professor é feita pelo admin.
                    telaAtual = "login"
                },
                onVoltarLoginClick = {
                    telaAtual = "login"
                }
            )
        }

        usuarioLogado != null -> {
            val cargo = usuarioLogado?.cargo
            if (cargo == Cargo.ADMIN) {
                AppMonitorFanAdmin(
                    onLogout = {
                        telaAtual = "login"
                    }
                )
            } else {
                AppMonitorFanLogado(
                    onLogout = {
                        telaAtual = "login"
                    }
                )
            }
        }
    }
}
