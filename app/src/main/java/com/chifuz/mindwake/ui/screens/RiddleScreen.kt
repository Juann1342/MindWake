package com.chifuz.mindwake.ui.screens

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Configuración de pantalla
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    // Funciones de escalado combinando ancho y alto
    fun scaledDp(percentWidth: Float = 0f, percentHeight: Float = 0f): Dp {
        val dpWidth = screenWidth * percentWidth
        val dpHeight = screenHeight * percentHeight
        return ((dpWidth + dpHeight) / 2).dp
    }

    fun scaledSp(percentWidth: Float = 0f, percentHeight: Float = 0f): TextUnit {
        val spWidth = screenWidth * percentWidth
        val spHeight = screenHeight * percentHeight
        return ((spWidth + spHeight) / 2).sp
    }

    state?.let { riddleState ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = scaledDp(percentWidth = 0.04f, percentHeight = 0.02f))
                .padding(top = scaledDp(percentWidth = 0f, percentHeight = 0.06f),
                    bottom = scaledDp(percentWidth = 0f, percentHeight = 0.18f))
        ) {
            val (
                header, contentArea, pista, verPista, pistasRestantes, verRespuesta, titleRiddle
            ) = createRefs()

            //  HEADER
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
                val iconSize = scaledDp(percentWidth = 0.12f, percentHeight = 0.08f)
                Icon(
                    painter = painterResource(R.drawable.ic_sleepy),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(scaledDp(percentWidth = 0.02f, percentHeight = 0.02f)),
                    tint = Color.DarkGray
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(scaledDp(percentWidth = 0f, percentHeight = 0.02f))
                        .padding(horizontal = scaledDp(percentWidth = 0.03f, percentHeight = 0f)),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor
                )

                Icon(
                    painter = painterResource(R.drawable.ic_smiling),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(scaledDp(percentWidth = 0.02f, percentHeight = 0.02f)),
                    tint = Color.DarkGray
                )
            }

            // 🏷 Título tipo de riddle
            Text(
                text = if (riddleState.riddle.type == RiddleType.RIDDLE) "ACERTIJO" else "PENSAMIENTO LATERAL",
                fontSize = scaledSp(percentWidth = 0.06f, percentHeight = 0.04f),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.constrainAs(titleRiddle) {
                    top.linkTo(header.bottom, margin = scaledDp(percentWidth = 0f, percentHeight = 0.02f))
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            //  ÁREA CENTRAL (con scroll)
            val scrollState = rememberScrollState()
            val questionTextSize = remember {
                val scaledValue = (screenWidth * 0.060f + screenHeight * 0.040f) / 2
                val spValue = scaledValue.sp
                // Convertimos a Float y limitamos
                TextUnit(
                    value = spValue.value.coerceAtMost(45f),
                    type = TextUnitType.Sp
                )
            }




            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = scaledDp(percentWidth = 0.05f, percentHeight = 0f))
                    .verticalScroll(scrollState)
                    .constrainAs(contentArea) {
                        top.linkTo(titleRiddle.bottom, margin = scaledDp(percentWidth = 0f, percentHeight = 0.03f))
                        bottom.linkTo(pistasRestantes.top, margin = scaledDp(percentWidth = 0f, percentHeight = 0.03f))
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = riddleState.riddle.question,
                    fontSize = questionTextSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = questionTextSize * 1.3f, // 🔹 30% más alto que el tamaño de texto
                    modifier = Modifier.fillMaxWidth()
                )

            }

            //  Pistas restantes
            Text(
                "PISTAS RESTANTES: ${riddleState.riddle.hints.size - riddleState.hintIndex}",
                fontSize = scaledSp(percentWidth = 0.03f, percentHeight = 0.025f),
                modifier = Modifier.constrainAs(pistasRestantes) {
                    top.linkTo(contentArea.bottom, margin = scaledDp(percentWidth = 0f, percentHeight = 0.02f))
                    bottom.linkTo(verPista.top, margin = scaledDp(percentWidth = 0f, percentHeight = 0.01f))
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // 🔍 Botón "Ver pista"
            Button(
                onClick = { viewModel.showNextHint() },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(scaledDp(percentWidth = 0f, percentHeight = 0.14f))
                    .constrainAs(verPista) {
                        top.linkTo(pistasRestantes.bottom, margin = scaledDp(percentWidth = 0f, percentHeight = 0.02f))
                        bottom.linkTo(pista.top, margin = scaledDp(percentWidth = 0f, percentHeight = 0.02f))
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(scaledDp(percentWidth = 0.02f, percentHeight = 0.02f)),
            ) {
                Text("Ver pista", fontSize = scaledSp(percentWidth = 0.035f, percentHeight = 0.03f))
            }

            //  Pista mostrada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = scaledDp(percentWidth = 0f, percentHeight = 0.06f))
                    .constrainAs(pista) {
                        top.linkTo(verPista.bottom, margin = scaledDp(percentWidth = 0f, percentHeight = 0.02f))
                        bottom.linkTo(verRespuesta.top, margin = scaledDp(percentWidth = 0f, percentHeight = 0.02f))
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (riddleState.hintIndex > 0) {
                    Text(
                        riddleState.riddle.hints[riddleState.hintIndex - 1],
                        fontSize = scaledSp(percentWidth = 0.05f, percentHeight = 0.04f),
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,

                        )
                }
            }

            //  Botón "Ver respuesta"
            Button(
                onClick = { viewModel.showAnswer() },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(scaledDp(percentWidth = 0f, percentHeight = 0.14f))
                    .constrainAs(verRespuesta) {
                        top.linkTo(pista.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(scaledDp(percentWidth = 0.02f, percentHeight = 0.02f))
            ) {
                Text("Ver respuesta", fontSize = scaledSp(percentWidth = 0.035f, percentHeight = 0.03f))
            }

            //  Efecto que abre el diálogo cuando cambia el estado
            LaunchedEffect(riddleState.isAnswerShown) {
                if (riddleState.isAnswerShown) {
                    showAnswerDialog = true
                }
            }
        }

//  DIÁLOGO DE RESPUESTA
        if (showAnswerDialog && riddleState.isAnswerShown) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                text = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Respuesta",
                                fontSize = scaledSp(percentWidth = 0.045f, percentHeight = 0.035f),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = riddleState.riddle.answer,
                                fontSize = scaledSp(percentWidth = 0.045f, percentHeight = 0.035f),
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                lineHeight = scaledSp(percentWidth = 0.05f, percentHeight = 0.04f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
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
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    if (riddleState.riddle.type == RiddleType.LATERAL) "Finalizar" else "Siguiente",
                                    fontSize = scaledSp(percentWidth = 0.035f, percentHeight = 0.03f)
                                )
                            }
                        }
                    }
                }
            )
        }



//  DIÁLOGO FINAL
        if (showFinalDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                text = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "¡Listo para comenzar!",
                                fontSize = scaledSp(percentWidth = 0.045f, percentHeight = 0.035f),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Ya estás listo para comenzar el día con la mente despierta!",
                                fontSize = scaledSp(percentWidth = 0.04f, percentHeight = 0.035f),
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                lineHeight = scaledSp(percentWidth = 0.045f, percentHeight = 0.035f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    showFinalDialog = false
                                    onFinishSession()
                                    scope.launch { viewModel.loadNext() }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Volver al inicio",
                                    fontSize = scaledSp(percentWidth = 0.035f, percentHeight = 0.03f)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}
