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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import br.com.monitorfan.dados.Monitoria
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.BorderSoft
import br.com.monitorfan.ui.theme.CardColor
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.MonitoriaViewModel
import br.com.monitorfan.ui.viewmodel.MonitoriaViewModelFactory

@Composable
fun TelaHome(
    monitoriaViewModel: MonitoriaViewModel = viewModel(factory = MonitoriaViewModelFactory(LocalContext.current))
) {
    val usuario = Repositorio.usuarioLogado.value ?: return

    val todasDoCurso by monitoriaViewModel.monitoriasDoCurso.collectAsState()

    val disciplinas = listOf("Todas") + todasDoCurso.map { it.disciplina }.distinct()

    var filtroSelecionado by remember { mutableStateOf("Todas") }
    var busca by remember { mutableStateOf("") }

    val monitoriasVisiveis = todasDoCurso
        .filter { filtroSelecionado == "Todas" || it.disciplina == filtroSelecionado }
        .filter { m ->
            if (busca.isBlank()) return@filter true
            val termo = busca.trim().lowercase()
            val monitor = Repositorio.buscarUsuario(m.monitorId)?.nome?.lowercase().orEmpty()
            m.disciplina.lowercase().contains(termo) || monitor.contains(termo)
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
            Text(text = "Olá, ${usuario.nome.substringBefore(" ")} 👋", color = GrayText, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Text("Monitorias ", color = WhiteSoft, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Text("do Curso", color = OrangePrimary, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = usuario.curso, color = GrayText, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(22.dp))

            SearchField(value = busca, onValueChange = { busca = it })

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("DISCIPLINAS")

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(disciplinas.size) { index ->
                    val disciplina = disciplinas[index]
                    CourseChip(
                        text = disciplina,
                        selected = disciplina == filtroSelecionado,
                        onClick = { filtroSelecionado = disciplina }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("MONITORIAS DISPONÍVEIS")

            Spacer(modifier = Modifier.height(16.dp))

            if (monitoriasVisiveis.isEmpty()) {
                EstadoVazio(
                    texto = if (todasDoCurso.isEmpty())
                        "Nenhuma monitoria cadastrada ainda para o seu curso."
                    else
                        "Nenhuma monitoria encontrada com esse filtro."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(monitoriasVisiveis) { item ->
                        val responsavel = Repositorio.buscarUsuario(item.monitorId)
                        if (responsavel != null) {
                            MonitoriaCard(item, responsavel)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EstadoVazio(texto: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, color = GrayText, fontSize = 14.sp)
    }
}

@Composable
fun SearchField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "Buscar disciplina ou monitor...", color = GrayText.copy(alpha = 0.9f)) },
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
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, color = GrayText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
}

@Composable
fun CourseChip(text: String, selected: Boolean, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) OrangePrimary else FieldColor,
        border = null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else GrayText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp)
        )
    }
}

@Composable
fun MonitoriaCard(item: Monitoria, responsavel: Usuario) {
    val ehProfessor = responsavel.cargo == Cargo.PROFESSOR
    val borderColor = if (ehProfessor) OrangePrimary.copy(alpha = 0.85f) else BorderSoft.copy(alpha = 0.40f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.linearGradient(colors = listOf(OrangePrimary, BluePrimary))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials(responsavel.nome), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = responsavel.nome, color = WhiteSoft, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    CargoBadge(responsavel.cargo)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.disciplina, color = GrayText, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${item.diaSemana} · ${item.horario}", color = GrayText, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CargoBadge(cargo: Cargo) {
    val (cor, fundo) = when (cargo) {
        Cargo.PROFESSOR -> OrangePrimary to OrangePrimary.copy(alpha = 0.18f)
        Cargo.MONITOR -> WhiteSoft to FieldColor
        else -> GrayText to FieldColor
    }
    Surface(shape = RoundedCornerShape(50), color = fundo) {
        Text(
            text = cargo.rotulo,
            color = cor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

fun initials(nome: String): String {
    val parts = nome.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}
