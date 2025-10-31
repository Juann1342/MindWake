package com.chifuz.mindwake.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chifuz.mindwake.ui.screens.HomeScreen
import com.chifuz.mindwake.ui.screens.RiddleScreen
import com.chifuz.mindwake.viewmodel.RiddleViewModel

@Composable
fun MindWakeNavGraph(
    navController: NavHostController,
    viewModel: RiddleViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Pantalla inicial
        composable("home") {
            HomeScreen(
                onStartClick = { navController.navigate("riddle") }
            )
        }

        // Pantalla de acertijos / ejercicios
        composable("riddle") {
            RiddleScreen(
                viewModel = viewModel,
                onFinishSession = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
