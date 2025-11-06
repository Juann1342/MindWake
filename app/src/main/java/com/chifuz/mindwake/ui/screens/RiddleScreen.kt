package com.chifuz.mindwake.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var showFinalDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val progress = when (viewModel.getCycleProgress()) {
        0 -> 0.3f
        1 -> 0.6f
        2 -> 1.0f
        else -> 0f
    }

    state?.let { riddleState ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(top = 32.dp, bottom = 80.dp) // 👈 padding superior global
        ) {
            val (
                header, contentArea, nextRow, pista, verPista, pistasRestantes, respuesta, verRespuesta
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

            // 🌌 ÁREA CENTRAL: el acertijo ocupa todo el espacio entre header y botón siguiente
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(contentArea) {
                        top.linkTo(header.bottom, margin = 16.dp)
                        bottom.linkTo(pistasRestantes.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints // 👈 ocupa todo el alto disponible
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // centra el contenido dentro del espacio
            ) {
                // 🧩 PREGUNTA
                Text(
                    text = riddleState.riddle.question,
                    style = MaterialTheme.typography.titleLarge
                )
            }
                Spacer(Modifier.height(16.dp))

                // 🔢 Pistas restantes

                Text(
                    "PISTAS RESTANTES: ${riddleState.riddle.hints.size - riddleState.hintIndex}", modifier = Modifier
                        .constrainAs(pistasRestantes) {
                        top.linkTo(contentArea.bottom, margin = 16.dp)
                        bottom.linkTo(verPista.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Spacer(Modifier.height(8.dp))

                // 💡 Botón "Ver pista"
                Button(
                    onClick = { viewModel.showNextHint() },
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // 70% del ancho
                        .height(50.dp)
                        .constrainAs(verPista) {
                            top.linkTo(pistasRestantes.bottom, margin = 16.dp)
                            bottom.linkTo(pista.top, margin = 16.dp)
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

                // 📜 Pista (dentro de un Row fijo)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .constrainAs(pista) {
                            top.linkTo(verPista.bottom, margin = 16.dp)
                            bottom.linkTo(verRespuesta.top, margin = 16.dp)
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

                // ✅ Botón "Ver respuesta"
                Button(
                    onClick = {
                        viewModel.showAnswer()
                        if (riddleState.riddle.type == RiddleType.LATERAL && riddleState.isAnswerShown) {
                            showFinalDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // 70% del ancho
                        .height(50.dp)     // altura fija,
                        .constrainAs(verRespuesta) {
                            top.linkTo(pista.bottom, margin = 16.dp)
                            bottom.linkTo(respuesta.top, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ver respuesta")
                }

                // 🧠 Respuesta (espacio reservado)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp, max = 80.dp) // deja espacio reservado pero limitado
                    .constrainAs(respuesta) {
                    top.linkTo(verRespuesta.bottom, margin = 16.dp)
                    bottom.linkTo(nextRow.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (riddleState.isAnswerShown) {
                        Text(
                            "Respuesta: ${riddleState.riddle.answer}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }


            // ⏭️ BOTÓN SIGUIENTE (parte inferior)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp)

                    .constrainAs(nextRow) {
                        top.linkTo(respuesta.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (riddleState.isAnswerShown && riddleState.riddle.type != RiddleType.LATERAL) {
                    Button(
                        onClick = { scope.launch { viewModel.loadNext() } }, modifier = Modifier
                            .fillMaxWidth(0.7f) // 70% del ancho
                            .height(50.dp),     // altura fija,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Siguiente")
                    }
                }
            }
        }

        // 🎉 DIÁLOGO FINAL
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

// 🔍 Extensión auxiliar
fun RiddleViewModel.getCycleProgress(): Int {
    val field = this::class.java.getDeclaredField("cycleIndex")
    field.isAccessible = true
    return field.getInt(this)
}
