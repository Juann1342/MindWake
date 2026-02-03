package com.chifuz.mindwake.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.chifuz.mindwake.R

@Composable
fun HomeScreen(onStartClick: () -> Unit) {

    // Configuración de pantalla para el escalado dinámico
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    // Funciones de escalado para mantener la proporción en distintos dispositivos
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

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly, // Distribuye el espacio de forma más armónica
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = scaledDp(percentWidth = 0.08f, percentHeight = 0f))
                .padding(vertical = scaledDp(percentWidth = 0f, percentHeight = 0.05f))
        ) {
            // Contenedor superior para Logo y textos
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo principal: protagonista visual
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.content_desc_logo),
                    modifier = Modifier.size(scaledDp(percentWidth = 0.50f, percentHeight = 0.40f))
                )

                Spacer(modifier = Modifier.height(scaledDp(percentWidth = 0f, percentHeight = 0.02f)))

                // Subtítulo con el nuevo enfoque de "dosis diaria"
                Text(
                    text = stringResource(R.string.home_subtitle),
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    lineHeight = scaledSp(percentWidth = 0.05f, percentHeight = 0.04f),
                    fontSize = scaledSp(percentWidth = 0.045f, percentHeight = 0.035f),
                    fontWeight = FontWeight.Medium
                )
            }

            // Botón de acción principal con tamaño ajustado para mejor estética
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .width(scaledDp(percentWidth = 1.6f, percentHeight = 0f))
                    .height(scaledDp(percentWidth = 0f, percentHeight = 0.12f)), // Reducido de 0.15f para mayor elegancia
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(percent = 50) // Botón redondeado, más moderno
            ) {
                Text(
                    text = stringResource(R.string.btn_start),
                    fontSize = scaledSp(percentWidth = 0.042f, percentHeight = 0.032f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}