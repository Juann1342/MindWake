package com.chifuz.mindwake.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
    val progress by viewModel.progressFlow.collectAsState(initial = 0.3f)
    var showAnswerDialog by remember { mutableStateOf(false) }
    var showFinalDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    state?.let { riddleState ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(top = 32.dp, bottom = 80.dp)
        ) {
            val (
                header, contentArea, pista, verPista, pistasRestantes, verRespuesta
            ) = createRefs()

            // 🧭 HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sleepy),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(8.dp),
                    tint = Color.DarkGray
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor
                )

                Icon(
                    painter = painterResource(R.drawable.ic_smiling),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(8.dp),
                    tint = Color.DarkGray
                )
            }

            // 🌌 ÁREA CENTRAL (con scroll)
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .constrainAs(contentArea) {
                        top.linkTo(header.bottom, margin = 16.dp)
                        bottom.linkTo(pistasRestantes.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = riddleState.riddle.question,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.height(16.dp))

            // 🧩 Pistas restantes
            Text(
                "PISTAS RESTANTES: ${riddleState.riddle.hints.size - riddleState.hintIndex}",
                modifier = Modifier.constrainAs(pistasRestantes) {
                    top.linkTo(contentArea.bottom, margin = 16.dp)
                    bottom.linkTo(verPista.top, margin = 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Spacer(Modifier.height(8.dp))

            // 🔍 Botón "Ver pista"
            Button(
                onClick = { viewModel.showNextHint() },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
                    .constrainAs(verPista) {
                        top.linkTo(pistasRestantes.bottom, margin = 8.dp)
                        bottom.linkTo(pista.top, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.DarkGray)
            ) {
                Text("Ver pista")
            }

            // 💡 Pista mostrada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 20.dp)
                    .constrainAs(pista) {
                        top.linkTo(verPista.bottom, margin = 8.dp)
                        bottom.linkTo(verRespuesta.top, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (riddleState.hintIndex > 0) {
                    Text(
                        riddleState.riddle.hints[riddleState.hintIndex - 1],
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // 💬 Botón "Ver respuesta"
            Button(
                onClick = {
                    viewModel.showAnswer()
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
                    .constrainAs(verRespuesta) {
                        top.linkTo(pista.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Ver respuesta")
            }

            // 🧠 Efecto que abre el diálogo cuando cambia el estado
            LaunchedEffect(riddleState.isAnswerShown) {
                if (riddleState.isAnswerShown) {
                    showAnswerDialog = true
                }
            }
        }

        // 💬 DIÁLOGO DE RESPUESTA
        if (showAnswerDialog && riddleState.isAnswerShown) {
            AlertDialog(
                onDismissRequest = { showAnswerDialog = false },
                title = { Text("Respuesta") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = riddleState.riddle.answer,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAnswerDialog = false
                            if (riddleState.riddle.type == RiddleType.LATERAL) {
                                showFinalDialog = true
                            } else {
                                scope.launch { viewModel.loadNext() }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            if (riddleState.riddle.type == RiddleType.LATERAL)
                                "Finalizar"
                            else
                                "Siguiente"
                        )
                    }
                }
            )
        }

        // 🌅 DIÁLOGO FINAL
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
