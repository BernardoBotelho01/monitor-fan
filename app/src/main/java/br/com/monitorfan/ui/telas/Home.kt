package br.com.monitorfan.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BluePrimary = Color(0xFF303C60)
private val BlueDark = Color(0xFF1F2942)
private val CardColor = Color(0xFF24314F)
private val FieldColor = Color(0xFF2A3656)
private val OrangePrimary = Color(0xFFF17535)
private val WhiteSoft = Color(0xFFF5F7FA)
private val GrayText = Color(0xFFB8C1D1)
private val BorderSoft = Color(0xFF415173)

data class Monitoria(
    val nome: String,
    val disciplina: String,
    val curso: String,
    val horario: String,
    val destaque: Boolean = false
)

@Composable
fun Home() {
    val cursos = listOf("Todos", "Computação", "Engenharia", "Matemática")
    val monitorias = listOf(
        Monitoria("Ana Martins", "Cálculo I", "Computação", "Hoje 14h"),
        Monitoria("Rafael Oliveira", "ED & Algoritmos", "Computação", "Amanhã 10h", true),
        Monitoria("Julia Santos", "Física II", "Engenharia", "Qua 16h"),
        Monitoria("Pedro Lima", "Álgebra Linear", "Matemática", "Hoje 16h")
    )

    val selectedTab by remember { mutableStateOf(0) }

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
                    FeedPreviewCard()
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedIndex = selectedTab
        )
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
                color = GrayText
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
            focusedBorderColor = BorderSoft,
            unfocusedBorderColor = BorderSoft,
            focusedTextColor = WhiteSoft,
            unfocusedTextColor = WhiteSoft,
            cursorColor = OrangePrimary
        )
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = GrayText,
        fontSize = 16.sp,
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
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderSoft),
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else GrayText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp)
        )
    }
}

@Composable
fun MonitoriaCard(item: Monitoria) {
    val borderColor = if (item.destaque) OrangePrimary else BorderSoft
    val badgeColor = if (item.destaque) OrangePrimary.copy(alpha = 0.18f) else BluePrimary
    val badgeTextColor = if (item.destaque) OrangePrimary else WhiteSoft

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CardColor),
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
fun FeedPreviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "FEED DE DÚVIDAS",
                color = GrayText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Publique dúvidas sobre disciplinas e receba respostas de monitores e outros alunos.",
                color = WhiteSoft,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SuggestionChip("Cálculo")
                SuggestionChip("Algoritmos")
                SuggestionChip("Física")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Abrir feed",
                    fontWeight = FontWeight.SemiBold
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
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Text(
            text = text,
            color = GrayText,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0
) {
    val items = listOf("Início", "Horários", "Feed", "Perfil")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.CalendarMonth,
        Icons.Default.ChatBubbleOutline,
        Icons.Default.Person
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        shape = RoundedCornerShape(26.dp),
        color = BlueDark,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item,
                        tint = if (selected) OrangePrimary else GrayText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item,
                        color = if (selected) OrangePrimary else GrayText,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun initials(nome: String): String {
    val parts = nome.trim().split(" ")
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

@Preview
@Composable
fun HomePreview() {
    Home()
}