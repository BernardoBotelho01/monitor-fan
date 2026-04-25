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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.dados.Cargo
import br.com.monitorfan.dados.Duvida
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.DuvidaViewModel
import br.com.monitorfan.ui.viewmodel.DuvidaViewModelFactory

private val CardColor = Color(0xFF24314F)
private val BorderSoft = Color(0xFF5A6A96)

@Composable
fun TelaFeedDuvida(
    onNovaDuvidaClick: () -> Unit = {},
    onAbrirDuvida: (Long) -> Unit = {},
    duvidaViewModel: DuvidaViewModel = viewModel(factory = DuvidaViewModelFactory(LocalContext.current))
) {
    val usuario = Repositorio.usuarioLogado.value ?: return

    val duvidasDoCurso by duvidaViewModel.duvidas.collectAsState()

    val disciplinas = listOf("Todas") + duvidasDoCurso.map { it.disciplina }.distinct()

    var filtro by remember { mutableStateOf("Todas") }
    var busca by remember { mutableStateOf("") }

    val visiveis = duvidasDoCurso
        .filter { filtro == "Todas" || it.disciplina == filtro }
        .filter { d ->
            if (busca.isBlank()) return@filter true
            val termo = busca.trim().lowercase()
            d.titulo.lowercase().contains(termo) ||
                d.disciplina.lowercase().contains(termo) ||
                d.descricao.lowercase().contains(termo)
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
            Text(text = "Comunidade", color = GrayText, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Text("Feed de ", color = WhiteSoft, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text("Dúvidas", color = OrangePrimary, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Dúvidas e respostas do curso de ${usuario.curso}.",
                color = GrayText,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = busca,
                onValueChange = { busca = it },
                placeholder = { Text(text = "Buscar dúvida, disciplina ou assunto...", color = GrayText.copy(alpha = 0.9f)) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GrayText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(20.dp),
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

            Spacer(modifier = Modifier.height(18.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(disciplinas.size) { index ->
                    val disciplina = disciplinas[index]
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (disciplina == filtro) OrangePrimary else FieldColor,
                        modifier = Modifier.clickable { filtro = disciplina }
                    ) {
                        Text(
                            text = disciplina,
                            color = if (disciplina == filtro) Color.White else GrayText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (visiveis.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (duvidasDoCurso.isEmpty())
                            "Ainda não há dúvidas publicadas para o seu curso.\nSeja o primeiro a perguntar!"
                        else "Nenhuma dúvida encontrada com esse filtro.",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(visiveis) { duvida ->
                        FeedPostCard(duvida = duvida, onClick = { onAbrirDuvida(duvida.id) })
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = onNovaDuvidaClick,
            containerColor = OrangePrimary,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Nova dúvida")
        }
    }
}

@Composable
fun FeedPostCard(duvida: Duvida, onClick: () -> Unit = {}) {
    val autor = Repositorio.buscarUsuario(duvida.autorId)
    val nomeAutor = autor?.nome ?: "Usuário"
    val cargoAutor = autor?.cargo ?: Cargo.USUARIO
    val respondidaPorMonitorOuProfessor = duvida.respostas.any { resposta ->
        val autorResposta = Repositorio.buscarUsuario(resposta.autorId)
        autorResposta?.cargo == Cargo.MONITOR || autorResposta?.cargo == Cargo.PROFESSOR
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(width = 1.dp, color = BorderSoft.copy(alpha = 0.35f), shape = RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(brush = Brush.linearGradient(colors = listOf(OrangePrimary, BluePrimary))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initials(nomeAutor), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = nomeAutor, color = WhiteSoft, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        CargoBadge(cargoAutor)
                    }
                    Text(text = duvida.disciplina, color = GrayText, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = duvida.titulo, color = WhiteSoft, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = duvida.descricao, color = GrayText, fontSize = 14.sp, lineHeight = 21.sp, maxLines = 3)

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (respondidaPorMonitorOuProfessor) {
                    Surface(shape = RoundedCornerShape(50), color = OrangePrimary.copy(alpha = 0.18f)) {
                        Text(
                            text = "Respondida por monitor/professor",
                            color = OrangePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Surface(shape = RoundedCornerShape(50), color = FieldColor) {
                    Text(
                        text = "${duvida.respostas.size} respostas",
                        color = WhiteSoft,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
