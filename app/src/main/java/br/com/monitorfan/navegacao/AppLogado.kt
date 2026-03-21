package br.com.monitorfan.navegacao

import com.example.appexemplo.ui.telas.TelaFeedDuvida
import com.example.appexemplo.ui.telas.TelaHome
import com.example.appexemplo.ui.telas.TelaPerfil
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
import androidx.compose.ui.unit.dp
import br.com.monitorfan.ui.telas.TelaNovaDuvida

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
    var mostrarNovaDuvida by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { screens.size }
    )

    LaunchedEffect(currentScreen) {
        pagerState.animateScrollToPage(screens.indexOf(currentScreen))
    }

    LaunchedEffect(pagerState.currentPage) {
        currentScreen = screens[pagerState.currentPage]
        if (currentScreen != ScreenItem.Feed) {
            mostrarNovaDuvida = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!mostrarNovaDuvida) {
                NavigationBar(
                    containerColor = Color(0xFF1F2942),
                    tonalElevation = 0.dp
                ) {
                    screens.forEach { screen ->
                        with(screen.bottomAppBarItem) {
                            NavigationBarItem(
                                selected = screen == currentScreen,
                                onClick = {
                                    currentScreen = screen
                                },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(label)
                                },
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
            userScrollEnabled = !mostrarNovaDuvida
        ) { page ->

            when (screens[page]) {
                ScreenItem.Home -> {
                    TelaHome()
                }

                ScreenItem.Feed -> {
                    if (mostrarNovaDuvida) {
                        TelaNovaDuvida(
                            onBackClick = {
                                mostrarNovaDuvida = false
                            },
                            onPublishClick = { _, _, _ ->
                                mostrarNovaDuvida = false
                            }
                        )
                    } else {
                        TelaFeedDuvida(
                            onNovaDuvidaClick = {
                                mostrarNovaDuvida = true
                            }
                        )
                    }
                }

                ScreenItem.Perfil -> {
                    TelaPerfil(
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

