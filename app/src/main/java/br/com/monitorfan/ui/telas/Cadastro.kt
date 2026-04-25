package br.com.monitorfan.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.dados.Cursos
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.AuthViewModel
import br.com.monitorfan.ui.viewmodel.AuthViewModelFactory
import br.com.monitorfan.ui.viewmodel.CadastroState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(
    onCadastrarClick: () -> Unit = {},
    onVoltarLoginClick: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
) {
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var curso by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var senha by remember { mutableStateOf(TextFieldValue("")) }
    var confirmarSenha by remember { mutableStateOf(TextFieldValue("")) }

    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }
    var cursoExpandido by remember { mutableStateOf(false) }

    val cadastroState by authViewModel.cadastroState.collectAsState()

    LaunchedEffect(cadastroState) {
        if (cadastroState is CadastroState.Sucesso) {
            authViewModel.resetCadastroState()
            onCadastrarClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueDark, BluePrimary)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text("Criar conta", color = WhiteSoft, fontSize = 30.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Preencha seus dados acadêmicos", color = GrayText, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Todo cadastro entra como usuário. A coordenação define monitores e professores depois.",
                color = GrayText.copy(alpha = 0.85f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            CadastroTextField(
                value = nome,
                onValueChange = {
                    nome = it
                    if (cadastroState is CadastroState.Erro) authViewModel.resetCadastroState()
                },
                label = "Nome",
                icon = { Icon(Icons.Default.Person, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            ExposedDropdownMenuBox(
                expanded = cursoExpandido,
                onExpandedChange = { cursoExpandido = !cursoExpandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = curso,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Curso") },
                    leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cursoExpandido) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = cursoExpandido,
                    onDismissRequest = { cursoExpandido = false },
                    modifier = Modifier.background(BluePrimary)
                ) {
                    Cursos.disponiveis.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item, color = WhiteSoft) },
                            onClick = {
                                curso = item
                                cursoExpandido = false
                                authViewModel.resetCadastroState()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CadastroTextField(
                value = matricula,
                onValueChange = {
                    matricula = it
                    if (cadastroState is CadastroState.Erro) authViewModel.resetCadastroState()
                },
                label = "Matrícula",
                icon = { Icon(Icons.Default.Badge, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            CadastroTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (cadastroState is CadastroState.Erro) authViewModel.resetCadastroState()
                },
                label = "E-mail",
                icon = { Icon(Icons.Default.Email, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = {
                    senha = it
                    if (cadastroState is CadastroState.Erro) authViewModel.resetCadastroState()
                },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null, tint = GrayText
                        )
                    }
                },
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = {
                    confirmarSenha = it
                    if (cadastroState is CadastroState.Erro) authViewModel.resetCadastroState()
                },
                label = { Text("Confirmar senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                        Icon(
                            imageVector = if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null, tint = GrayText
                        )
                    }
                },
                visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            if (cadastroState is CadastroState.Erro) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = (cadastroState as CadastroState.Erro).mensagem,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    authViewModel.cadastrar(nome.text, email.text, senha.text, confirmarSenha.text, curso, matricula.text)
                },
                enabled = cadastroState !is CadastroState.Carregando,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = Color.White
                )
            ) {
                if (cadastroState is CadastroState.Carregando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.height(24.dp))
                } else {
                    Text("Cadastrar", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TextButton(onClick = onVoltarLoginClick) {
                Text("Já tem conta?", color = WhiteSoft, fontWeight = FontWeight.Medium)
                Text(" Entrar", color = OrangePrimary, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CadastroTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    icon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = icon,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = textFieldColors()
    )
}

@Composable
fun textFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FieldColor,
        unfocusedContainerColor = FieldColor,
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = Color.Transparent,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = OrangePrimary,
        unfocusedLabelColor = GrayText,
        focusedLeadingIconColor = OrangePrimary,
        unfocusedLeadingIconColor = GrayText,
        cursorColor = OrangePrimary
    )
}
