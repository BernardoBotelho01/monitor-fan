package br.com.monitorfan.ui.telas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.BorderSoft
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.DuvidaViewModel
import br.com.monitorfan.ui.viewmodel.DuvidaViewModelFactory
import br.com.monitorfan.util.DisciplinasRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovaDuvida(
    onBackClick: () -> Unit = {},
    onDuvidaPublicada: () -> Unit = {},
    duvidaViewModel: DuvidaViewModel = viewModel(factory = DuvidaViewModelFactory(LocalContext.current))
) {
    val usuario = Repositorio.usuarioLogado.value ?: return
    val context = LocalContext.current

    val disciplinasDoCurso = remember(usuario.curso) {
        DisciplinasRepository.listarPorCurso(context, usuario.curso)
    }

    var titulo by remember { mutableStateOf(TextFieldValue("")) }
    var disciplina by remember { mutableStateOf(TextFieldValue("")) }
    var descricao by remember { mutableStateOf(TextFieldValue("")) }
    var expandirDisciplinas by remember { mutableStateOf(false) }

    val duvidaPublicada by duvidaViewModel.duvidaPublicada.collectAsState()
    var publicando by remember { mutableStateOf(false) }

    LaunchedEffect(duvidaPublicada) {
        if (duvidaPublicada) {
            duvidaViewModel.resetDuvidaPublicada()
            onDuvidaPublicada()
        }
    }

    val podePublicar =
        titulo.text.isNotBlank() &&
                disciplina.text.isNotBlank() &&
                descricao.text.isNotBlank() &&
                disciplinasDoCurso.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BlueDark, BluePrimary, BlueDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(
                        color = FieldColor,
                        shape = RoundedCornerShape(14.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = WhiteSoft
                    )
                }

                Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                Column {
                    Text(
                        text = usuario.curso,
                        color = GrayText,
                        fontSize = 14.sp
                    )

                    Row {
                        Text(
                            text = "Nova ",
                            color = WhiteSoft,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Dúvida",
                            color = OrangePrimary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Sua dúvida ficará visível apenas para os usuários, monitores e professores do seu curso.",
                color = GrayText,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título da dúvida") },
                placeholder = { Text("Ex: Como resolver limite com fatoração?") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = novaDuvidaTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandirDisciplinas,
                onExpandedChange = {
                    if (disciplinasDoCurso.isNotEmpty()) {
                        expandirDisciplinas = !expandirDisciplinas
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = disciplina.text,
                    onValueChange = {},
                    readOnly = true,
                    enabled = disciplinasDoCurso.isNotEmpty(),
                    label = { Text("Disciplina") },
                    placeholder = {
                        Text(
                            text = if (disciplinasDoCurso.isEmpty()) {
                                "Nenhuma disciplina encontrada para este curso"
                            } else {
                                "Selecione a disciplina"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandirDisciplinas
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = novaDuvidaTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expandirDisciplinas,
                    onDismissRequest = { expandirDisciplinas = false },
                    modifier = Modifier.background(BluePrimary)
                ) {
                    disciplinasDoCurso.forEach { nomeDisciplina ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = nomeDisciplina,
                                    color = WhiteSoft
                                )
                            },
                            onClick = {
                                disciplina = TextFieldValue(nomeDisciplina)
                                expandirDisciplinas = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                placeholder = {
                    Text("Explique o que você já entendeu e em qual parte está travando...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null
                    )
                },
                minLines = 6,
                maxLines = 10,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = novaDuvidaTextFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FieldColor),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dica para receber respostas melhores",
                        color = OrangePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Inclua contexto, escolha a disciplina correta, diga o que você já tentou e onde exatamente está a dúvida.",
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            if (disciplinasDoCurso.isEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Nenhuma disciplina foi encontrada para o curso ${usuario.curso}. Verifique se o arquivo disciplinas.json está em app/src/main/assets.",
                    color = OrangePrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    publicando = true
                    duvidaViewModel.criarDuvida(
                        disciplina.text,
                        titulo.text,
                        descricao.text
                    )
                },
                enabled = podePublicar && !publicando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = OrangePrimary.copy(alpha = 0.45f),
                    disabledContentColor = Color.White.copy(alpha = 0.75f)
                )
            ) {
                if (publicando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Publicar dúvida",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WhiteSoft),
                border = BorderStroke(
                    width = 1.dp,
                    color = BorderSoft.copy(alpha = 0.45f)
                )
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun novaDuvidaTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FieldColor,
        unfocusedContainerColor = FieldColor,
        disabledContainerColor = FieldColor,

        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,

        focusedTextColor = WhiteSoft,
        unfocusedTextColor = WhiteSoft,
        disabledTextColor = GrayText,

        focusedLabelColor = OrangePrimary,
        unfocusedLabelColor = GrayText,
        disabledLabelColor = GrayText,

        focusedLeadingIconColor = OrangePrimary,
        unfocusedLeadingIconColor = GrayText,
        disabledLeadingIconColor = GrayText,

        focusedTrailingIconColor = OrangePrimary,
        unfocusedTrailingIconColor = GrayText,
        disabledTrailingIconColor = GrayText,

        focusedPlaceholderColor = GrayText.copy(alpha = 0.75f),
        unfocusedPlaceholderColor = GrayText.copy(alpha = 0.75f),
        disabledPlaceholderColor = GrayText.copy(alpha = 0.75f),

        cursorColor = OrangePrimary
    )
}