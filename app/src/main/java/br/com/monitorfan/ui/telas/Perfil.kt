package br.com.monitorfan.ui.telas

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.dados.Cursos
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.CardColor
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.AlterarSenhaState
import br.com.monitorfan.ui.viewmodel.EditarPerfilState
import br.com.monitorfan.ui.viewmodel.PerfilViewModel
import br.com.monitorfan.ui.viewmodel.PerfilViewModelFactory
import br.com.monitorfan.util.SessaoPrefs
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(
    onLogout: () -> Unit = {},
    perfilViewModel: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(LocalContext.current))
) {
    val usuario = Repositorio.usuarioLogado.value ?: return
    val context = LocalContext.current
    val editarState by perfilViewModel.state.collectAsState()
    val alterarSenhaState by perfilViewModel.alterarSenhaState.collectAsState()

    var modoEdicao by remember { mutableStateOf(false) }
    var mostrarDialogSenha by remember { mutableStateOf(false) }
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var senhaAtualVisivel by remember { mutableStateOf(false) }
    var novaSenhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }

    var novoNome by remember { mutableStateOf(TextFieldValue(usuario.nome)) }
    var novoEmail by remember { mutableStateOf(TextFieldValue(usuario.email)) }
    var novaMatricula by remember { mutableStateOf(TextFieldValue(usuario.matricula)) }
    var novoCurso by remember { mutableStateOf(usuario.curso) }
    var novaFotoUri by remember { mutableStateOf<String?>(usuario.fotoUri) }
    var cursoExpandido by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}
            novaFotoUri = uri.toString()
        }
    }

    LaunchedEffect(editarState) {
        if (editarState is EditarPerfilState.Sucesso) {
            modoEdicao = false
            perfilViewModel.resetState()
        }
    }

    LaunchedEffect(alterarSenhaState) {
        if (alterarSenhaState is AlterarSenhaState.Sucesso) {
            mostrarDialogSenha = false
            senhaAtual = ""
            novaSenha = ""
            confirmarSenha = ""
            perfilViewModel.resetAlterarSenhaState()
        }
    }

    fun entrarEdicao() {
        novoNome = TextFieldValue(usuario.nome)
        novoEmail = TextFieldValue(usuario.email)
        novaMatricula = TextFieldValue(usuario.matricula)
        novoCurso = usuario.curso
        novaFotoUri = usuario.fotoUri
        modoEdicao = true
        perfilViewModel.resetState()
    }

    fun cancelarEdicao() {
        modoEdicao = false
        novaFotoUri = usuario.fotoUri
        perfilViewModel.resetState()
    }

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
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (modoEdicao) "Editando" else "Perfil",
                    color = GrayText,
                    fontSize = 18.sp
                )
                if (!modoEdicao) {
                    IconButton(onClick = { entrarEdicao() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil",
                            tint = OrangePrimary
                        )
                    }
                }
            }

            Text(
                text = if (modoEdicao) "Editar Perfil" else "Minha Conta",
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Avatar
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (novaFotoUri == null) OrangePrimary else Color.Transparent,
                    modifier = Modifier
                        .size(96.dp)
                        .then(
                            if (modoEdicao) Modifier.clickable { photoPickerLauncher.launch("image/*") }
                            else Modifier
                        )
                ) {
                    if (novaFotoUri != null) {
                        AsyncImage(
                            model = novaFotoUri,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
                }
                if (modoEdicao) {
                    Surface(
                        shape = CircleShape,
                        color = OrangePrimary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Alterar foto",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (modoEdicao) novoNome.text.ifBlank { "Seu nome" } else usuario.nome,
                color = WhiteSoft,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = OrangePrimary.copy(alpha = 0.18f)
            ) {
                Text(
                    text = usuario.cargo.rotulo,
                    color = OrangePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (modoEdicao) novoCurso.ifBlank { usuario.curso } else usuario.curso,
                color = GrayText,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (modoEdicao) {
                // Campos editáveis
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = FieldColor),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = novoNome,
                            onValueChange = {
                                novoNome = it
                                if (editarState is EditarPerfilState.Erro) perfilViewModel.resetState()
                            },
                            label = { Text("Nome") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )

                        ExposedDropdownMenuBox(
                            expanded = cursoExpandido,
                            onExpandedChange = { cursoExpandido = !cursoExpandido },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = novoCurso,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Curso") },
                                leadingIcon = {
                                    Icon(Icons.Default.MenuBook, contentDescription = null)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = cursoExpandido)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
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
                                            novoCurso = item
                                            cursoExpandido = false
                                            perfilViewModel.resetState()
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = novaMatricula,
                            onValueChange = {
                                novaMatricula = it
                                if (editarState is EditarPerfilState.Erro) perfilViewModel.resetState()
                            },
                            label = { Text("Matrícula") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )

                        OutlinedTextField(
                            value = novoEmail,
                            onValueChange = {
                                novoEmail = it
                                if (editarState is EditarPerfilState.Erro) perfilViewModel.resetState()
                            },
                            label = { Text("E-mail") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                    }
                }

                if (editarState is EditarPerfilState.Erro) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = (editarState as EditarPerfilState.Erro).mensagem,
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        perfilViewModel.salvarPerfil(
                            usuario = usuario,
                            novoNome = novoNome.text,
                            novoEmail = novoEmail.text,
                            novaMatricula = novaMatricula.text,
                            novoCurso = novoCurso,
                            novaFotoUri = novaFotoUri
                        )
                    },
                    enabled = editarState !is EditarPerfilState.Carregando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (editarState is EditarPerfilState.Carregando) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                        Text("Salvar alterações", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { cancelarEdicao() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GrayText)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Text("Cancelar", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                // Campos somente leitura
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = FieldColor),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        InfoItem(label = "Matrícula", value = usuario.matricula)
                        InfoItem(label = "E-mail", value = usuario.email)
                        InfoItem(label = "Curso", value = usuario.curso)
                        InfoItem(label = "Usuário", value = usuario.cargo.rotulo)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        senhaAtual = ""
                        novaSenha = ""
                        confirmarSenha = ""
                        perfilViewModel.resetAlterarSenhaState()
                        mostrarDialogSenha = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, OrangePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Text("Alterar senha", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        SessaoPrefs.limpar(context)
                        Repositorio.encerrarSessao()
                        onLogout()
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
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Text(
                        text = "Sair da conta",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (mostrarDialogSenha) {
            AlertDialog(
                onDismissRequest = {
                    if (alterarSenhaState !is AlterarSenhaState.Carregando) {
                        mostrarDialogSenha = false
                        perfilViewModel.resetAlterarSenhaState()
                    }
                },
                title = {
                    Text("Alterar Senha", color = WhiteSoft, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = senhaAtual,
                            onValueChange = {
                                senhaAtual = it
                                if (alterarSenhaState is AlterarSenhaState.Erro) perfilViewModel.resetAlterarSenhaState()
                            },
                            label = { Text("Senha atual") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { senhaAtualVisivel = !senhaAtualVisivel }) {
                                    Icon(
                                        imageVector = if (senhaAtualVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (senhaAtualVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = novaSenha,
                            onValueChange = {
                                novaSenha = it
                                if (alterarSenhaState is AlterarSenhaState.Erro) perfilViewModel.resetAlterarSenhaState()
                            },
                            label = { Text("Nova senha") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { novaSenhaVisivel = !novaSenhaVisivel }) {
                                    Icon(
                                        imageVector = if (novaSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (novaSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = confirmarSenha,
                            onValueChange = {
                                confirmarSenha = it
                                if (alterarSenhaState is AlterarSenhaState.Erro) perfilViewModel.resetAlterarSenhaState()
                            },
                            label = { Text("Confirmar nova senha") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                                    Icon(
                                        imageVector = if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        if (alterarSenhaState is AlterarSenhaState.Erro) {
                            Text(
                                text = (alterarSenhaState as AlterarSenhaState.Erro).mensagem,
                                color = Color(0xFFFF6B6B),
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            perfilViewModel.alterarSenha(usuario, senhaAtual, novaSenha, confirmarSenha)
                        },
                        enabled = alterarSenhaState !is AlterarSenhaState.Carregando,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        if (alterarSenhaState is AlterarSenhaState.Carregando) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Salvar")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogSenha = false
                            perfilViewModel.resetAlterarSenhaState()
                        },
                        enabled = alterarSenhaState !is AlterarSenhaState.Carregando
                    ) {
                        Text("Cancelar", color = GrayText)
                    }
                },
                containerColor = BluePrimary
            )
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = GrayText, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = WhiteSoft, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
