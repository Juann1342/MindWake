package com.chifuz.mindwake.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chifuz.mindwake.viewmodel.RiddleViewModel

@Composable
fun RiddleScreen(
    viewModel: RiddleViewModel,
    onFinishSession: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showFinalDialog by remember { mutableStateOf(false) }

    state?.let { riddleState ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Text(text = riddleState.riddle.question, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Pistas disponibles: ${riddleState.riddle.hints.size - riddleState.hintIndex}")
            Button(onClick = { viewModel.showNextHint() }) {
                Text("Ver pista")
            }

            riddleState.hintIndex.takeIf { it > 0 }?.let { index ->
                Text(riddleState.riddle.hints[index - 1])
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.showAnswer()
                if (viewModel.isLastLateral()) showFinalDialog = true
            }) {
                Text("Ver respuesta")
            }

            if (riddleState.isAnswerShown) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Respuesta: ${riddleState.riddle.answer}", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (riddleState.isAnswerShown && !viewModel.isLastLateral()) {
                Button(onClick = { viewModel.loadNext() }) {
                    Text("Siguiente")
                }
            }
        }

        if (showFinalDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("¡Listo para comenzar!") },
                text = { Text("Ya estás listo para comenzar el día con la mente despierta!") },
                confirmButton = {
                    Button(onClick = onFinishSession) {
                        Text("Volver al inicio")
                    }
                }
            )
        }
    }
}
