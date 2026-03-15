package com.example.presence

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.presence.presentation.login.LoginScreen
import com.example.presence.presentation.main.MainScreen
import com.example.presence.presentation.register.RegisterScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object RegisterRoute

@Serializable
data class MainRoute(val email: String)


@Composable
fun App() {
    val navController: NavHostController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()


    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = RegisterRoute,
        ) {
            composable<LoginRoute> {
                LoginScreen(navHostController = navController)
            }
            composable<RegisterRoute> {
                RegisterScreen(navHostController = navController)
            }
            composable<MainRoute> { navBackStackEntry ->
                val mainRoute: MainRoute = navBackStackEntry.toRoute()
                MainScreen(navHostController = navController, email = mainRoute.email)
            }
        }
    }
}