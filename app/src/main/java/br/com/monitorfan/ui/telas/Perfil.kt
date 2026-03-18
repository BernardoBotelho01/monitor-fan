package com.example.appexemplo.ui.telas


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
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
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Perfil",
                color = GrayText,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Minha Conta",
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                shape = CircleShape,
                color = OrangePrimary,
                modifier = Modifier.size(96.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Lucas Andrade",
                color = WhiteSoft,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Engenharia de Software",
                color = GrayText,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "5º Período • Conclusão 2027",
                color = GrayText,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FieldColor),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    InfoItem(label = "Matrícula", value = "202312345")
                    InfoItem(label = "E-mail", value = "lucas@email.com")
                    InfoItem(label = "Curso", value = "Engenharia de Software")
                    InfoItem(label = "Período", value = "5º Período")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                Text(
                    text = "Sair da conta",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = GrayText,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = WhiteSoft,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}