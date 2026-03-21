package com.example.appexemplo.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft


@Composable
fun TelaCadastro(
    onCadastrarClick: () -> Unit = {},
    onVoltarLoginClick: () -> Unit = {}
) {
    var nome by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var periodo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var anoConclusao by remember { mutableStateOf("") }

    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }
    var expandido by remember { mutableStateOf(false) }

    val periodos = listOf(
        "1º Período", "2º Período", "3º Período", "4º Período", "5º Período",
        "6º Período", "7º Período", "8º Período", "9º Período", "10º Período"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BlueDark, BluePrimary))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Criar conta",
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Preencha seus dados acadêmicos",
                color = GrayText,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            CadastroTextField(
                value = nome,
                onValueChange = { nome = it },
                label = "Nome",
                icon = { Icon(Icons.Default.Person, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            CadastroTextField(
                value = curso,
                onValueChange = { curso = it },
                label = "Curso",
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            CadastroTextField(
                value = matricula,
                onValueChange = { matricula = it },
                label = "Matrícula",
                icon = { Icon(Icons.Default.Badge, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = periodo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Período") },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = GrayText
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandido = true },
                    colors = textFieldColors()
                )

                DropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    periodos.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                periodo = item
                                expandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CadastroTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-mail",
                icon = { Icon(Icons.Default.Email, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = GrayText
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
                onValueChange = { confirmarSenha = it },
                label = { Text("Confirmar senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                        Icon(
                            imageVector = if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = GrayText
                        )
                    }
                },
                visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            CadastroTextField(
                value = anoConclusao,
                onValueChange = { anoConclusao = it },
                label = "Ano de conclusão",
                icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onCadastrarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Cadastrar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            TextButton(onClick = onVoltarLoginClick) {
                Text(
                    text = "Já tem conta? Entrar",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CadastroTextField(
    value: String,
    onValueChange: (String) -> Unit,
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