package br.com.monitorfan.navegacao

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.monitorfan.ui.telas.TelaDetalheDuvida
import br.com.monitorfan.ui.telas.TelaFeedDuvida
import br.com.monitorfan.ui.telas.TelaHome
import br.com.monitorfan.ui.telas.TelaNovaDuvida
import br.com.monitorfan.ui.telas.TelaPerfil

class BottomAppBarItem(
    val icon: ImageVector,
    val label: String
)

sealed class ScreenItem(
    val bottomAppBarItem: BottomAppBarItem
) {
    data object Home : ScreenItem(
        bottomAppBarItem = BottomAppBarItem(Icons.Default.Home, "Início")
    )

    data object Feed : ScreenItem(
        bottomAppBarItem = BottomAppBarItem(Icons.Default.ChatBubbleOutline, "Feed")
    )

    data object Perfil : ScreenItem(
        bottomAppBarItem = BottomAppBarItem(Icons.Default.Person, "Perfil")
    )
}

/**
 * Estados possíveis dentro da aba Feed.
 */
private sealed class EstadoFeed {
    data object Lista : EstadoFeed()
    data object NovaDuvida : EstadoFeed()
    data class Detalhe(val duvidaId: Long) : EstadoFeed()
}

@Composable
fun AppMonitorFanLogado(
    onLogout: () -> Unit = {}
) {
    val screens = remember {
        listOf(
            ScreenItem.Home,
            ScreenItem.Feed,
            ScreenItem.Perfil
        )
    }

    var currentScreen by remember { mutableStateOf<ScreenItem>(ScreenItem.Home) }
    var estadoFeed by remember { mutableStateOf<EstadoFeed>(EstadoFeed.Lista) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { screens.size }
    )

    LaunchedEffect(currentScreen) {
        pagerState.animateScrollToPage(screens.indexOf(currentScreen))
    }

    LaunchedEffect(pagerState.currentPage) {
        currentScreen = screens[pagerState.currentPage]
        // Ao sair do feed, volta para a listagem
        if (currentScreen != ScreenItem.Feed) {
            estadoFeed = EstadoFeed.Lista
        }
    }

    // Só esconde a bottom bar em sub-telas do feed (nova dúvida / detalhe)
    val escondeBottomBar = currentScreen == ScreenItem.Feed && estadoFeed !is EstadoFeed.Lista

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!escondeBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF1F2942),
                    tonalElevation = 0.dp
                ) {
                    screens.forEach { screen ->
                        with(screen.bottomAppBarItem) {
                            NavigationBarItem(
                                selected = screen == currentScreen,
                                onClick = { currentScreen = screen },
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
        }
    ) { innerPadding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
            userScrollEnabled = !escondeBottomBar
        ) { page ->

            when (screens[page]) {
                ScreenItem.Home -> {
                    TelaHome()
                }

                ScreenItem.Feed -> {
                    when (val estado = estadoFeed) {
                        is EstadoFeed.Lista -> {
                            TelaFeedDuvida(
                                onNovaDuvidaClick = {
                                    estadoFeed = EstadoFeed.NovaDuvida
                                },
                                onAbrirDuvida = { id ->
                                    estadoFeed = EstadoFeed.Detalhe(id)
                                }
                            )
                        }

                        is EstadoFeed.NovaDuvida -> {
                            TelaNovaDuvida(
                                onBackClick = { estadoFeed = EstadoFeed.Lista },
                                onDuvidaPublicada = { estadoFeed = EstadoFeed.Lista }
                            )
                        }

                        is EstadoFeed.Detalhe -> {
                            TelaDetalheDuvida(
                                duvidaId = estado.duvidaId,
                                onBackClick = { estadoFeed = EstadoFeed.Lista }
                            )
                        }
                    }
                }

                ScreenItem.Perfil -> {
                    TelaPerfil(onLogout = onLogout)
                }
            }
        }
    }
}
