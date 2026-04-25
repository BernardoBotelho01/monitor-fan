package br.com.monitorfan.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Cursos
import br.com.monitorfan.dados.Usuario
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.CardColor
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.AdminUsuariosViewModel
import br.com.monitorfan.ui.viewmodel.AdminUsuariosViewModelFactory

@Composable
fun TelaAdminUsuarios(
    adminViewModel: AdminUsuariosViewModel = viewModel(factory = AdminUsuariosViewModelFactory(LocalContext.current))
) {
    val filtros = listOf("Todos") + Cursos.disponiveis
    var filtroCurso by remember { mutableStateOf("Todos") }
    var busca by remember { mutableStateOf("") }

    val todos by adminViewModel.usuarios.collectAsState()

    val visiveis = todos
        .filter { filtroCurso == "Todos" || it.curso == filtroCurso }
        .filter { u ->
            if (busca.isBlank()) true
            else {
                val t = busca.trim().lowercase()
                u.nome.lowercase().contains(t) || u.email.lowercase().contains(t) || u.matricula.lowercase().contains(t)
            }
        }

    var usuarioSelecionado by remember { mutableStateOf<Usuario?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(BlueDark, BluePrimary, BlueDark)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(text = "Administração", color = GrayText, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(6.dp))

            Row {
                Text("Gerenciar ", color = WhiteSoft, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                Text("Usuários", color = OrangePrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Defina quem é usuário, monitor ou professor.", color = GrayText, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = busca,
                onValueChange = { busca = it },
                placeholder = { Text(text = "Buscar por nome, e-mail ou matrícula", color = GrayText.copy(alpha = 0.9f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldColor,
                    unfocusedContainerColor = FieldColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft,
                    focusedPlaceholderColor = GrayText,
                    unfocusedPlaceholderColor = GrayText,
                    cursorColor = OrangePrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtros.size) { idx ->
                    val nome = filtros[idx]
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (nome == filtroCurso) OrangePrimary else FieldColor,
                        modifier = Modifier.clickable { filtroCurso = nome }
                    ) {
                        Text(
                            text = nome,
                            color = if (nome == filtroCurso) Color.White else GrayText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (visiveis.isEmpty()) {
                Text(text = "Nenhum usuário encontrado.", color = GrayText, fontSize = 14.sp, modifier = Modifier.padding(top = 24.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(visiveis) { usuario ->
                        UsuarioCard(usuario = usuario, onClick = { usuarioSelecionado = usuario })
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    usuarioSelecionado?.let { alvo ->
        DialogoAlterarCargo(
            usuario = alvo,
            onConfirmar = { novoCargo ->
                adminViewModel.alterarCargo(alvo.id, novoCargo)
                usuarioSelecionado = null
            },
            onDismiss = { usuarioSelecionado = null }
        )
    }
}

@Composable
private fun UsuarioCard(usuario: Usuario, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.linearGradient(colors = listOf(OrangePrimary, BluePrimary))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials(usuario.nome), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = usuario.nome, color = WhiteSoft, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = "${usuario.curso} · ${usuario.matricula}", color = GrayText, fontSize = 12.sp)
            }

            CargoBadge(usuario.cargo)
        }
    }
}

@Composable
private fun DialogoAlterarCargo(
    usuario: Usuario,
    onConfirmar: (Cargo) -> Unit,
    onDismiss: () -> Unit
) {
    var cargoEscolhido by remember { mutableStateOf(usuario.cargo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlueDark,
        titleContentColor = WhiteSoft,
        textContentColor = GrayText,
        title = { Text(text = "Definir cargo", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(text = usuario.nome, color = WhiteSoft, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(text = usuario.curso, color = GrayText, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(16.dp))

                listOf(Cargo.USUARIO, Cargo.MONITOR, Cargo.PROFESSOR).forEach { cargo ->
                    val selecionado = cargoEscolhido == cargo
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selecionado) OrangePrimary.copy(alpha = 0.22f) else FieldColor,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { cargoEscolhido = cargo }
                    ) {
                        Text(
                            text = cargo.rotulo,
                            color = if (selecionado) OrangePrimary else WhiteSoft,
                            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(cargoEscolhido) }) {
                Text("Salvar", color = OrangePrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GrayText) }
        }
    )
}

