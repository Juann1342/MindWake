package com.chifuz.mindwake.ui.screens

/*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chifuz.mindwake.viewmodel.RiddleViewModel
import com.mindwake.app.viewmodel.RiddleViewModel

@Composable
fun StatsScreen(navController: NavController, viewModel: RiddleViewModel) {
    val solved = viewModel.solvedCount.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Estadísticas", style = MaterialTheme.typography.headlineSmall)
            Text("Acertijos resueltos: ${solved.value}")
            Button(onClick = { navController.navigateUp() }) {
                Text("Volver")
            }
        }
    }
}
*/