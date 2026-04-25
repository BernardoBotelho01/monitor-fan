package br.com.monitorfan.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.CardColor
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.DuvidaViewModel
import br.com.monitorfan.ui.viewmodel.DuvidaViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun TelaDetalheDuvida(
    duvidaId: Long,
    onBackClick: () -> Unit = {},
    duvidaViewModel: DuvidaViewModel = viewModel(factory = DuvidaViewModelFactory(LocalContext.current))
) {
    val usuario = Repositorio.usuarioLogado.value ?: return

    LaunchedEffect(duvidaId) {
        duvidaViewModel.carregarDuvida(duvidaId)
    }

    val duvida by duvidaViewModel.duvidaDetalhe.collectAsState()
    val respostaEnviada by duvidaViewModel.respostaEnviada.collectAsState()
    val duvidaDeletada by duvidaViewModel.duvidaDeletada.collectAsState()
    val duvidaEditada by duvidaViewModel.duvidaEditada.collectAsState()

    var textoResposta by remember { mutableStateOf(TextFieldValue("")) }
    var modoEdicao by remember { mutableStateOf(false) }
    var confirmarDelete by remember { mutableStateOf(false) }
    var editandoRespostaId by remember { mutableStateOf<Long?>(null) }
    var editTextoResposta by remember { mutableStateOf(TextFieldValue("")) }
    var confirmarDeleteRespostaId by remember { mutableStateOf<Long?>(null) }

    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(respostaEnviada) {
        if (respostaEnviada) {
            textoResposta = TextFieldValue("")
            duvidaViewModel.resetRespostaEnviada()
        }
    }

    LaunchedEffect(duvidaDeletada) {
        if (duvidaDeletada) {
            duvidaViewModel.resetDuvidaDeletada()
            onBackClick()
        }
    }

    LaunchedEffect(duvidaEditada) {
        if (duvidaEditada) {
            modoEdicao = false
            duvidaViewModel.resetDuvidaEditada()
        }
    }

    if (duvida == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BlueDark, BluePrimary, BlueDark)))
        ) {
            Text(text = "Carregando...", color = GrayText, modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    val duvidaAtual = duvida!!
    val autor = Repositorio.buscarUsuario(duvidaAtual.autorId)
    val ehAutor = usuario.id == duvidaAtual.autorId
    val ehAdmin = usuario.cargo == Cargo.ADMIN
    val ehMonitorOuProf = usuario.cargo == Cargo.MONITOR || usuario.cargo == Cargo.PROFESSOR
    // Quem pode enviar no campo de texto: monitor/professor OU o autor da dúvida
    val podeEscrever = ehMonitorOuProf || ehAutor

    var editTitulo by remember(duvidaAtual.titulo) { mutableStateOf(TextFieldValue(duvidaAtual.titulo)) }
    var editDisciplina by remember(duvidaAtual.disciplina) { mutableStateOf(TextFieldValue(duvidaAtual.disciplina)) }
    var editDescricao by remember(duvidaAtual.descricao) { mutableStateOf(TextFieldValue(duvidaAtual.descricao)) }

    if (confirmarDelete) {
        AlertDialog(
            onDismissRequest = { confirmarDelete = false },
            containerColor = CardColor,
            title = { Text("Excluir dúvida", color = WhiteSoft, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Tem certeza? Esta ação remove a dúvida e todas as respostas permanentemente.",
                    color = GrayText
                )
            },
            confirmButton = {
                Button(
                    onClick = { confirmarDelete = false; duvidaViewModel.deletarDuvida(duvidaId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Excluir", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarDelete = false }) {
                    Text("Cancelar", color = GrayText)
                }
            }
        )
    }

    if (confirmarDeleteRespostaId != null) {
        val rid = confirmarDeleteRespostaId!!
        AlertDialog(
            onDismissRequest = { confirmarDeleteRespostaId = null },
            containerColor = CardColor,
            title = { Text("Excluir resposta", color = WhiteSoft, fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza? Esta resposta será removida permanentemente.", color = GrayText) },
            confirmButton = {
                Button(
                    onClick = {
                        confirmarDeleteRespostaId = null
                        duvidaViewModel.deletarResposta(duvidaId, rid)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Excluir", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarDeleteRespostaId = null }) {
                    Text("Cancelar", color = GrayText)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(BlueDark, BluePrimary, BlueDark)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Cabeçalho
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(color = FieldColor, shape = RoundedCornerShape(14.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = WhiteSoft)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = duvidaAtual.disciplina, color = GrayText, fontSize = 14.sp)
                    Text(
                        text = if (modoEdicao) "Editar Dúvida" else "Dúvida",
                        color = WhiteSoft,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                if (ehAutor || ehAdmin) {
                    if (!modoEdicao) {
                        IconButton(onClick = { modoEdicao = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = OrangePrimary)
                        }
                        IconButton(onClick = { confirmarDelete = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color(0xFFD32F2F))
                        }
                    } else {
                        IconButton(onClick = { modoEdicao = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar edição", tint = GrayText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                // Card da dúvida
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (modoEdicao) OrangePrimary else OrangePrimary.copy(alpha = 0.45f),
                                RoundedCornerShape(22.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = CardColor),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AvatarRedondo(nome = autor?.nome ?: "?", tamanho = 48)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = autor?.nome ?: "Usuário",
                                            color = WhiteSoft,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        CargoBadge(autor?.cargo ?: Cargo.USUARIO)
                                    }
                                    Text(text = duvidaAtual.disciplina, color = GrayText, fontSize = 13.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (modoEdicao) {
                                OutlinedTextField(
                                    value = editTitulo,
                                    onValueChange = { editTitulo = it },
                                    label = { Text("Título") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editFieldColors()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = editDisciplina,
                                    onValueChange = { editDisciplina = it },
                                    label = { Text("Disciplina") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editFieldColors()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = editDescricao,
                                    onValueChange = { editDescricao = it },
                                    label = { Text("Descrição") },
                                    minLines = 4,
                                    maxLines = 8,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editFieldColors()
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        duvidaViewModel.editarDuvida(
                                            duvidaId, editTitulo.text, editDisciplina.text, editDescricao.text
                                        )
                                    },
                                    enabled = editTitulo.text.isNotBlank() && editDisciplina.text.isNotBlank() && editDescricao.text.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OrangePrimary,
                                        contentColor = Color.White,
                                        disabledContainerColor = OrangePrimary.copy(alpha = 0.4f),
                                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                                    )
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Salvar alterações", fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                Text(
                                    text = duvidaAtual.titulo,
                                    color = WhiteSoft,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = duvidaAtual.descricao,
                                    color = GrayText,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = if (duvidaAtual.respostas.isEmpty()) "Ainda sem respostas"
                               else "Respostas (${duvidaAtual.respostas.size})",
                        color = WhiteSoft,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Lista de respostas
                items(duvidaAtual.respostas) { resposta ->
                    val autorResposta = Repositorio.buscarUsuario(resposta.autorId)
                    val respostaDeMonitorOuProf = autorResposta?.cargo == Cargo.MONITOR ||
                                                  autorResposta?.cargo == Cargo.PROFESSOR
                    val ehAutorResposta = usuario.id == resposta.autorId
                    val podeEditarResposta = ehAutorResposta &&
                            (usuario.cargo == Cargo.MONITOR || usuario.cargo == Cargo.PROFESSOR)
                    val estaEditando = editandoRespostaId == resposta.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .then(
                                if (respostaDeMonitorOuProf)
                                    Modifier.border(1.dp, OrangePrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                                else Modifier
                            ),
                        colors = CardDefaults.cardColors(containerColor = FieldColor),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AvatarRedondo(nome = autorResposta?.nome ?: "?", tamanho = 40)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = autorResposta?.nome ?: "Usuário",
                                            color = WhiteSoft,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        CargoBadge(autorResposta?.cargo ?: Cargo.USUARIO)
                                    }
                                }
                                if (podeEditarResposta && !estaEditando) {
                                    IconButton(
                                        onClick = {
                                            editandoRespostaId = resposta.id
                                            editTextoResposta = TextFieldValue(resposta.texto)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar resposta", tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { confirmarDeleteRespostaId = resposta.id },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Excluir resposta", tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (estaEditando) {
                                OutlinedTextField(
                                    value = editTextoResposta,
                                    onValueChange = { editTextoResposta = it },
                                    minLines = 2,
                                    maxLines = 6,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editFieldColors()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            duvidaViewModel.editarResposta(duvidaId, resposta.id, editTextoResposta.text)
                                            editandoRespostaId = null
                                        },
                                        enabled = editTextoResposta.text.isNotBlank(),
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = Color.White)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Salvar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    TextButton(
                                        onClick = { editandoRespostaId = null },
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Text("Cancelar", color = GrayText, fontSize = 14.sp)
                                    }
                                }
                            } else {
                                Text(text = resposta.texto, color = WhiteSoft, fontSize = 14.sp, lineHeight = 20.sp)

                                if (respostaDeMonitorOuProf && ehAutor) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = {
                                            val primeiroNome = autorResposta?.nome
                                                ?.split(" ")?.firstOrNull() ?: "Monitor"
                                            textoResposta = TextFieldValue("@$primeiroNome ")
                                            scope.launch {
                                                listState.animateScrollToItem(duvidaAtual.respostas.size + 1)
                                            }
                                            focusRequester.requestFocus()
                                        },
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                            horizontal = 0.dp, vertical = 0.dp
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Reply,
                                            contentDescription = null,
                                            tint = OrangePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Responder",
                                            color = OrangePrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }

            // Campo de texto — visível para monitor/professor e para o autor da dúvida
            if (podeEscrever) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = textoResposta,
                        onValueChange = { textoResposta = it },
                        placeholder = {
                            Text(
                                if (ehMonitorOuProf) "Escreva sua resposta..."
                                else "Responda ao monitor ou professor..."
                            )
                        },
                        minLines = 2,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,
                            focusedPlaceholderColor = GrayText,
                            unfocusedPlaceholderColor = GrayText,
                            cursorColor = OrangePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (textoResposta.text.isBlank()) return@Button
                            duvidaViewModel.responderDuvida(duvidaAtual.id, textoResposta.text)
                        },
                        enabled = textoResposta.text.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = Color.White,
                            disabledContainerColor = OrangePrimary.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (ehMonitorOuProf) "Enviar resposta" else "Enviar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FieldColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Apenas monitores e professores respondem. Quando houver resposta, você poderá interagir.",
                        color = GrayText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = BluePrimary,
    unfocusedContainerColor = BluePrimary,
    focusedBorderColor = OrangePrimary,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = WhiteSoft,
    unfocusedTextColor = WhiteSoft,
    focusedLabelColor = OrangePrimary,
    unfocusedLabelColor = GrayText,
    cursorColor = OrangePrimary
)

@Composable
private fun AvatarRedondo(nome: String, tamanho: Int) {
    Box(
        modifier = Modifier
            .size(tamanho.dp)
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(colors = listOf(OrangePrimary, BluePrimary))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials(nome),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (tamanho / 2.8).toInt().sp
        )
    }
}
