package br.com.monitorfan.navegacao

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.monitorfan.ui.telas.Cadastro
import br.com.monitorfan.ui.telas.Home
import br.com.monitorfan.ui.telas.Login

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Cadastro : Screen("cadastro")
    object Home : Screen("home")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            Login(
                onLoginClick = { _, _ ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onCadastroClick = {
                    navController.navigate(Screen.Cadastro.route)
                }
            )
        }

        composable(Screen.Cadastro.route) {
            Cadastro(
                onRegisterClick = { _, _, _, _, _, _, _, _ ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            Home()
        }
    }
}