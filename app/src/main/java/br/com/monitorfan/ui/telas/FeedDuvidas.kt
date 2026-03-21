package com.example.appexemplo.ui.telas


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
import br.com.monitorfan.ui.theme.FieldColor
import br.com.monitorfan.ui.theme.GrayText
import br.com.monitorfan.ui.theme.OrangePrimary
import br.com.monitorfan.ui.theme.WhiteSoft

private val CardColor = Color(0xFF24314F)
private val BorderSoft = Color(0xFF5A6A96)

data class PostDuvida(
    val autor: String,
    val disciplina: String,
    val titulo: String,
    val descricao: String,
    val respostas: Int,
    val curtidas: Int,
    val monitorRespondeu: Boolean
)

@Composable
fun TelaFeedDuvida(
    onNovaDuvidaClick: () -> Unit = {}
) {
    val categorias = listOf("Todas", "Cálculo", "Algoritmos", "Física")

    val posts = listOf(
        PostDuvida(
            autor = "Lucas Andrade",
            disciplina = "Cálculo I",
            titulo = "Como resolver limite com indeterminação 0/0?",
            descricao = "Estou travando na parte em que preciso fatorar antes de simplificar a expressão.",
            respostas = 6,
            curtidas = 12,
            monitorRespondeu = true
        ),
        PostDuvida(
            autor = "Marina Souza",
            disciplina = "Algoritmos",
            titulo = "Quando usar fila e quando usar pilha?",
            descricao = "Entendi a teoria, mas nos exercícios ainda confundo os casos de uso.",
            respostas = 4,
            curtidas = 6,
            monitorRespondeu = true
        ),
        PostDuvida(
            autor = "Pedro Lima",
            disciplina = "Física II",
            titulo = "Alguém tem uma forma mais fácil de entender campo elétrico?",
            descricao = "Principalmente a parte de direção, sentido e intensidade do vetor.",
            respostas = 9,
            curtidas = 15,
            monitorRespondeu = false
        ),
        PostDuvida(
            autor = "Ana Clara",
            disciplina = "Álgebra Linear",
            titulo = "Base e dimensão: como resolver mais rápido na prova?",
            descricao = "Queria um passo a passo para saber identificar isso sem me perder.",
            respostas = 3,
            curtidas = 5,
            monitorRespondeu = false
        )
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
                text = "Comunidade",
                color = GrayText,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Text(
                    text = "Feed de ",
                    color = WhiteSoft,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Dúvidas",
                    color = OrangePrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Pergunte, responda e aprenda com monitores e alunos.",
                color = GrayText,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        text = "Buscar dúvida, disciplina ou assunto...",
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

            Spacer(modifier = Modifier.height(18.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categorias.size) { index ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (index == 0) OrangePrimary else FieldColor,
                        modifier = Modifier
                    ) {
                        Text(
                            text = categorias[index],
                            color = if (index == 0) Color.White else GrayText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(posts) { post ->
                    FeedPostCard(post)
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = onNovaDuvidaClick,
            containerColor = OrangePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Nova dúvida"
            )
        }
    }
}

@Composable
fun FeedPostCard(post: PostDuvida) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BorderSoft.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(OrangePrimary, BluePrimary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials(post.autor),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = post.autor,
                        color = WhiteSoft,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Text(
                        text = post.disciplina,
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = post.titulo,
                color = WhiteSoft,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.descricao,
                color = GrayText,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (post.monitorRespondeu) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = OrangePrimary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Verified,
//                                contentDescription = null,
//                                tint = OrangePrimary,
//                                modifier = Modifier.size(16.dp)
//                            )
//
//                            Spacer(modifier = Modifier.width(6.dp))

//                            Text(
//                                text = "Monitor respondeu",
//                                color = OrangePrimary,
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Bold
//                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = FieldColor
                ) {
                    Text(
                        text = "${post.respostas} respostas",
                        color = WhiteSoft,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = FieldColor
                ) {
                    Text(
                        text = "${post.curtidas} curtidas",
                        color = GrayText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}