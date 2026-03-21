package com.example.appexemplo.ui.telas

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.monitorfan.ui.theme.BlueDark
import br.com.monitorfan.ui.theme.BluePrimary
import br.com.monitorfan.ui.theme.BorderSoft
import br.com.monitorfan.ui.theme.CardColor
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft


data class Monitoria(
    val nome: String,
    val disciplina: String,
    val curso: String,
    val horario: String,
    val destaque: Boolean = false
)

@Composable
fun TelaHome() {
    val cursos = listOf("Todos", "Computação", "Engenharia", "Matemática")
    val monitorias = listOf(
        Monitoria("Ana Martins", "Cálculo I", "Computação", "Hoje 14h"),
        Monitoria("Rafael Oliveira", "ED & Algoritmos", "Computação", "Amanhã 10h", true),
        Monitoria("Julia Santos", "Física II", "Engenharia", "Qua 16h"),
        Monitoria("Pedro Lima", "Álgebra Linear", "Matemática", "Hoje 16h")
    )

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
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Olá, Lucas 👋",
                color = GrayText,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Text(
                    text = "Monitorias ",
                    color = WhiteSoft,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Hoje",
                    color = OrangePrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            SearchField()

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("CURSOS")

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cursos.size) { index ->
                    CourseChip(
                        text = cursos[index],
                        selected = index == 0
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("MONITORIAS DISPONÍVEIS")

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(monitorias) { item ->
                    MonitoriaCard(item)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun SearchField() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        readOnly = true,
        placeholder = {
            Text(
                text = "Buscar disciplina ou monitor...",
                color = GrayText.copy(alpha = 0.9f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = GrayText
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
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
    Text(
        text = title,
        color = GrayText,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp
    )
}

@Composable
fun CourseChip(
    text: String,
    selected: Boolean
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) OrangePrimary else FieldColor,
        border = null,
        modifier = Modifier.clickable { }
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
fun MonitoriaCard(item: Monitoria) {
    val borderColor = if (item.destaque) {
        OrangePrimary.copy(alpha = 0.85f)
    } else {
        BorderSoft.copy(alpha = 0.40f)
    }

    val badgeColor = if (item.destaque) {
        OrangePrimary.copy(alpha = 0.20f)
    } else {
        FieldColor
    }

    val badgeTextColor = if (item.destaque) {
        OrangePrimary
    } else {
        WhiteSoft
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(OrangePrimary, BluePrimary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials(item.nome),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.nome,
                    color = WhiteSoft,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${item.disciplina} · ${item.curso}",
                    color = GrayText,
                    fontSize = 15.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = badgeColor
            ) {
                Text(
                    text = item.horario,
                    color = badgeTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}



@Composable
fun SuggestionChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = FieldColor,
        border = null
    ) {
        Text(
            text = text,
            color = GrayText,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
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