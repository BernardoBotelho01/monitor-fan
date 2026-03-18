package br.com.monitorfan.navegacao

import com.example.appexemplo.ui.telas.FeedScreen
import com.example.appexemplo.ui.telas.HomeScreen
import com.example.appexemplo.ui.telas.ProfileScreen
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
import br.com.monitorfan.ui.telas.NovaDuvida

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
                    HomeScreen()
                }

                ScreenItem.Feed -> {
                    if (mostrarNovaDuvida) {
                        NovaDuvida(
                            onBackClick = {
                                mostrarNovaDuvida = false
                            },
                            onPublishClick = { _, _, _ ->
                                mostrarNovaDuvida = false
                            }
                        )
                    } else {
                        FeedScreen(
                            onNovaDuvidaClick = {
                                mostrarNovaDuvida = true
                            }
                        )
                    }
                }

                ScreenItem.Perfil -> {
                    ProfileScreen(
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

