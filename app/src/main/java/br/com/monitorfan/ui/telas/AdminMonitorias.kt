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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitorfan.dados.Cursos
import br.com.monitorfan.dados.Monitoria
import br.com.monitorfan.dados.Repositorio
import br.com.monitorfan.dados.Usuario
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.CardColor
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft
import br.com.monitorfan.ui.viewmodel.MonitoriaViewModel
import br.com.monitorfan.ui.viewmodel.MonitoriaViewModelFactory

private val DIAS_SEMANA = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado")

@Composable
fun TelaAdminMonitorias(
    monitoriaViewModel: MonitoriaViewModel = viewModel(factory = MonitoriaViewModelFactory(LocalContext.current))
) {
    val filtros = listOf("Todos") + Cursos.disponiveis
    var filtroCurso by remember { mutableStateOf("Todos") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var monitoriaParaExcluir by remember { mutableStateOf<Monitoria?>(null) }
    var monitoriaParaEditar by remember { mutableStateOf<Monitoria?>(null) }

    val todasMonitorias by monitoriaViewModel.todasMonitorias.collectAsState()

    val visiveis = todasMonitorias
        .filter { filtroCurso == "Todos" || it.curso == filtroCurso }
        .sortedBy { it.curso + it.disciplina }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueDark, BluePrimary, BlueDark)))
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
                Text("Monitorias", color = OrangePrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Vincule monitores e professores às disciplinas e horários.", color = GrayText, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(18.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            if (visiveis.isEmpty()) {
                Text(text = "Nenhuma monitoria cadastrada. Toque em + para criar.", color = GrayText, fontSize = 14.sp, modifier = Modifier.padding(top = 20.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(visiveis) { m ->
                        MonitoriaAdminCard(
                            monitoria = m,
                            onEditar = { monitoriaParaEditar = m },
                            onRemover = { monitoriaParaExcluir = m }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(96.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarDialogo = true },
            containerColor = OrangePrimary,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nova monitoria")
        }
    }

    if (mostrarDialogo) {
        DialogoMonitoria(
            titulo = "Nova monitoria",
            onDismiss = { mostrarDialogo = false },
            onSalvar = { monitorId, disciplina, curso, dia, horario, sala ->
                monitoriaViewModel.cadastrarMonitoria(monitorId, disciplina, curso, dia, horario, sala)
                mostrarDialogo = false
            }
        )
    }

    monitoriaParaEditar?.let { alvo ->
        DialogoMonitoria(
            titulo = "Editar monitoria",
            monitoriaInicial = alvo,
            onDismiss = { monitoriaParaEditar = null },
            onSalvar = { monitorId, disciplina, curso, dia, horario, sala ->
                monitoriaViewModel.atualizarMonitoria(alvo.id, monitorId, disciplina, curso, dia, horario, sala)
                monitoriaParaEditar = null
            }
        )
    }

    monitoriaParaExcluir?.let { alvo ->
        AlertDialog(
            onDismissRequest = { monitoriaParaExcluir = null },
            containerColor = BlueDark,
            titleContentColor = WhiteSoft,
            textContentColor = GrayText,
            title = { Text("Remover monitoria", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja remover a monitoria de ${alvo.disciplina}?") },
            confirmButton = {
                TextButton(onClick = {
                    monitoriaViewModel.removerMonitoria(alvo.id)
                    monitoriaParaExcluir = null
                }) {
                    Text("Remover", color = OrangePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { monitoriaParaExcluir = null }) { Text("Cancelar", color = GrayText) }
            }
        )
    }
}

@Composable
private fun MonitoriaAdminCard(
    monitoria: Monitoria,
    onEditar: () -> Unit,
    onRemover: () -> Unit
) {
    val responsavel = Repositorio.buscarUsuario(monitoria.monitorId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(OrangePrimary, BluePrimary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials(responsavel?.nome ?: "?"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = monitoria.disciplina,
                    color = WhiteSoft,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                responsavel?.let {
                    CargoBadge(it.cargo)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "${responsavel?.nome ?: "Sem responsável"} · ${monitoria.curso}",
                    color = GrayText,
                    fontSize = 12.sp
                )

                Text(
                    text = "${monitoria.diaSemana} · ${monitoria.horario} · ${monitoria.sala}",
                    color = GrayText.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = OrangePrimary
                    )
                }

                IconButton(onClick = onRemover) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = GrayText
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoMonitoria(
    titulo: String,
    monitoriaInicial: Monitoria? = null,
    onDismiss: () -> Unit,
    onSalvar: (Long, String, String, String, String, String) -> Unit
) {
    val responsavelInicial = monitoriaInicial?.let { Repositorio.buscarUsuario(it.monitorId) }

    var disciplina by remember { mutableStateOf(monitoriaInicial?.disciplina ?: "") }
    var curso by remember { mutableStateOf(monitoriaInicial?.curso ?: "") }
    var diaSemana by remember { mutableStateOf(monitoriaInicial?.diaSemana ?: "") }
    var horario by remember { mutableStateOf(monitoriaInicial?.horario ?: "") }
    var sala by remember { mutableStateOf(monitoriaInicial?.sala ?: "") }
    var responsavelSelecionado by remember { mutableStateOf<Usuario?>(responsavelInicial) }

    var expandeCurso by remember { mutableStateOf(false) }
    var expandeDia by remember { mutableStateOf(false) }
    var expandeResponsavel by remember { mutableStateOf(false) }

    val responsaveisCurso = if (curso.isBlank()) emptyList()
    else Repositorio.responsaveisDoCurso(curso)

    val podeSalvar = responsavelSelecionado != null &&
            disciplina.isNotBlank() && curso.isNotBlank() &&
            diaSemana.isNotBlank() && horario.isNotBlank() && sala.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlueDark,
        titleContentColor = WhiteSoft,
        textContentColor = GrayText,
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                CampoDropdown(
                    valor = curso, label = "Curso", expandido = expandeCurso,
                    onExpandirChange = { expandeCurso = it },
                    opcoes = Cursos.disponiveis,
                    onSelecionar = { curso = it; responsavelSelecionado = null; expandeCurso = false }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = expandeResponsavel && curso.isNotBlank(),
                    onExpandedChange = { if (curso.isNotBlank()) expandeResponsavel = !expandeResponsavel },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = responsavelSelecionado?.let { "${it.nome} (${it.cargo.rotulo})" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        enabled = curso.isNotBlank(),
                        label = { Text("Responsável") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandeResponsavel) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = dialogoTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandeResponsavel && curso.isNotBlank(),
                        onDismissRequest = { expandeResponsavel = false },
                        modifier = Modifier.background(BluePrimary)
                    ) {
                        if (responsaveisCurso.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Nenhum monitor/professor neste curso", color = GrayText) },
                                onClick = { expandeResponsavel = false }
                            )
                        } else {
                            responsaveisCurso.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text("${u.nome} (${u.cargo.rotulo})", color = WhiteSoft) },
                                    onClick = { responsavelSelecionado = u; expandeResponsavel = false }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = disciplina, onValueChange = { disciplina = it },
                    label = { Text("Disciplina") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogoTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                CampoDropdown(
                    valor = diaSemana, label = "Dia da semana", expandido = expandeDia,
                    onExpandirChange = { expandeDia = it },
                    opcoes = DIAS_SEMANA,
                    onSelecionar = { diaSemana = it; expandeDia = false }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = horario, onValueChange = { horario = it },
                    label = { Text("Horário (ex: 14:00 - 16:00)") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogoTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = sala, onValueChange = { sala = it },
                    label = { Text("Sala / Laboratório") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogoTextFieldColors()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val r = responsavelSelecionado ?: return@TextButton
                    onSalvar(r.id, disciplina, curso, diaSemana, horario, sala)
                },
                enabled = podeSalvar
            ) {
                Text("Salvar", color = if (podeSalvar) OrangePrimary else GrayText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GrayText) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoDropdown(
    valor: String, label: String, expandido: Boolean,
    onExpandirChange: (Boolean) -> Unit, opcoes: List<String>, onSelecionar: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { onExpandirChange(!expandido) },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = valor, onValueChange = {}, readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = dialogoTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { onExpandirChange(false) },
            modifier = Modifier.background(BluePrimary)
        ) {
            opcoes.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = WhiteSoft) },
                    onClick = { onSelecionar(item) }
                )
            }
        }
    }
}

@Composable
private fun dialogoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FieldColor,
    unfocusedContainerColor = FieldColor,
    focusedBorderColor = OrangePrimary,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = WhiteSoft,
    unfocusedTextColor = WhiteSoft,
    focusedLabelColor = OrangePrimary,
    unfocusedLabelColor = GrayText,
    cursorColor = OrangePrimary
)
