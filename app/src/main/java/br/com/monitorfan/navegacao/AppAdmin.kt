package br.com.monitorfan.navegacao

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.monitorfan.ui.telas.TelaAdminMonitorias
import br.com.monitorfan.ui.telas.TelaAdminUsuarios
import br.com.monitorfan.ui.telas.TelaPerfil

private class AdminBarItem(
    val icon: ImageVector,
    val label: String
)

private sealed class TelaAdmin(val item: AdminBarItem) {
    data object Usuarios : TelaAdmin(AdminBarItem(Icons.Default.SupervisorAccount, "Usuários"))
    data object Monitorias : TelaAdmin(AdminBarItem(Icons.Default.Event, "Monitorias"))
    data object Perfil : TelaAdmin(AdminBarItem(Icons.Default.Person, "Perfil"))
}

@Composable
fun AppMonitorFanAdmin(
    onLogout: () -> Unit = {}
) {
    val telas = remember {
        listOf(TelaAdmin.Usuarios, TelaAdmin.Monitorias, TelaAdmin.Perfil)
    }

    var telaAtual by remember { mutableStateOf<TelaAdmin>(TelaAdmin.Usuarios) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { telas.size }
    )

    LaunchedEffect(telaAtual) {
        pagerState.animateScrollToPage(telas.indexOf(telaAtual))
    }

    LaunchedEffect(pagerState.currentPage) {
        telaAtual = telas[pagerState.currentPage]
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1F2942),
                tonalElevation = 0.dp
            ) {
                telas.forEach { tela ->
                    with(tela.item) {
                        NavigationBarItem(
                            selected = tela == telaAtual,
                            onClick = { telaAtual = tela },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFF17535),
                                selectedTextColor = Color(0xFFF17535),
                                unselectedIconColor = Color(0xFFB8C1D1),
                                unselectedTextColor = Color(0xFFB8C1D1),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (telas[page]) {
                TelaAdmin.Usuarios -> TelaAdminUsuarios()
                TelaAdmin.Monitorias -> TelaAdminMonitorias()
                TelaAdmin.Perfil -> TelaPerfil(onLogout = onLogout)
            }
        }
    }
}
