package com.chifuz.mindwake.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chifuz.mindwake.R
import com.chifuz.mindwake.data.repository.RiddleType
import com.chifuz.mindwake.viewmodel.RiddleViewModel
import kotlinx.coroutines.launch

@Composable
fun RiddleScreen(
    viewModel: RiddleViewModel,
    onFinishSession: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showFinalDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Calcula el progreso según el ciclo actual
    // (cycleIndex = 0 → primer acertijo, 1 → segundo, 2 → lateral)
    val progress = when (viewModel.getCycleProgress()) {
        0 -> 0.3f
        1 -> 0.6f
        2 -> 1.0f
        else -> 0f
    }

    state?.let { riddleState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado con íconos y barra de progreso
            Row(
                verticalAlignment = Alignment.CenterVertically, // Asegura alineación vertical
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painter = painterResource(R.drawable.ic_sleepy), contentDescription = null, modifier = Modifier.size(36.dp).padding(8.dp), tint = Color.DarkGray)


                // Barra de Progreso (ocupa el espacio restante)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f) // Esto hace que la barra ocupe todo el espacio libre restante
                        .height(8.dp), // Los padding que tenías en la barra los he movido/ajustado al Row y a los iconos
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )

                Icon(painter = painterResource(R.drawable.ic_smiling), contentDescription = null, modifier = Modifier.size(36.dp).padding(8.dp), tint = Color.DarkGray)

            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contenido principal
            Text(
                text = riddleState.riddle.question,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("PISTAS RESTANTES: ${riddleState.riddle.hints.size - riddleState.hintIndex}")

            Button(
                onClick = { viewModel.showNextHint() },
                modifier = Modifier.width(180.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.DarkGray)
            ) {
                Text("Ver pista")
            }

            riddleState.hintIndex.takeIf { it > 0 }?.let { index ->
                Text(riddleState.riddle.hints[index - 1])
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.showAnswer()
                    if (riddleState.riddle.type == RiddleType.LATERAL && riddleState.isAnswerShown) {
                        showFinalDialog = true
                    }
                },
                modifier = Modifier.width(180.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Ver respuesta")
            }

            if (riddleState.isAnswerShown) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Respuesta: ${riddleState.riddle.answer}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (riddleState.isAnswerShown && riddleState.riddle.type != RiddleType.LATERAL) {
                Button(onClick = {
                    scope.launch { viewModel.loadNext() }
                }) {
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
                    Button(onClick = {
                        showFinalDialog = false
                        onFinishSession()
                        scope.launch { viewModel.loadNext() }
                    }) {
                        Text("Volver al inicio")
                    }
                }
            )
        }
    }
}

// Extensión auxiliar del ViewModel para obtener el ciclo
fun RiddleViewModel.getCycleProgress(): Int {
    val field = this::class.java.getDeclaredField("cycleIndex")
    field.isAccessible = true
    return field.getInt(this)
}
