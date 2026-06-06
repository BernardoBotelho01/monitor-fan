package br.com.monitorfan.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.AuthViewModel
import br.com.monitorfan.ui.viewmodel.AuthViewModelFactory
import br.com.monitorfan.ui.viewmodel.RedefinirSenhaState

@Composable
fun TelaEsqueceuSenha(
    onVoltar: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }

    val state by authViewModel.redefinirSenhaState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.resetRedefinirSenhaState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueDark, BluePrimary, BlueDark)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Redefinir Senha",
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Informe o e-mail da sua conta e enviaremos um link para redefinir a senha.",
                color = GrayText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            if (state is RedefinirSenhaState.Sucesso) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "E-mail enviado!",
                    color = WhiteSoft,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Verifique sua caixa de entrada e siga as instruções para redefinir sua senha.",
                    color = GrayText,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = {
                        authViewModel.resetRedefinirSenhaState()
                        onVoltar()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Voltar ao login", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (state is RedefinirSenhaState.Erro) authViewModel.resetRedefinirSenhaState()
                    },
                    label = { Text("E-mail") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                if (state is RedefinirSenhaState.Erro) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = (state as RedefinirSenhaState.Erro).mensagem,
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { authViewModel.redefinirSenha(email.text) },
                    enabled = state !is RedefinirSenhaState.Carregando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (state is RedefinirSenhaState.Carregando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Enviar e-mail", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(onClick = onVoltar) {
                    Text("Voltar ao login", color = OrangePrimary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
