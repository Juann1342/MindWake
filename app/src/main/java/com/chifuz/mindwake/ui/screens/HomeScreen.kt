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

    //  Configuración de pantalla
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    // 🔧 Funciones de escalado dinámico
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
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = scaledDp(percentWidth = 0.06f, percentHeight = 0f))
                .padding(vertical = scaledDp(percentWidth = 0f, percentHeight = 0.04f))
        ) {
            //  Logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.content_desc_logo),
                modifier = Modifier.size(scaledDp(percentWidth = 0.45f, percentHeight = 0.35f))
            )

            Spacer(modifier = Modifier.height(scaledDp(percentWidth = 0f, percentHeight = 0.02f)))

            //  Título principal
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif,
                fontSize = scaledSp(percentWidth = 0.09f, percentHeight = 0.06f),
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(scaledDp(percentWidth = 0f, percentHeight = 0.02f)))

            //  Subtítulo
            Text(
                text = stringResource(R.string.home_subtitle),
                textAlign = TextAlign.Center,
                color = Color.DarkGray,
                fontSize = scaledSp(percentWidth = 0.04f, percentHeight = 0.03f)
            )

            Spacer(modifier = Modifier.height(scaledDp(percentWidth = 0f, percentHeight = 0.04f)))

            //  Botón principal
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .width(scaledDp(percentWidth = 0.9f, percentHeight = 0f))
                    .height(scaledDp(percentWidth = 0f, percentHeight = 0.15f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(scaledDp(percentWidth = 0.035f, percentHeight = 0.025f))
            ) {
                Text(
                    text = stringResource(R.string.btn_start),
                    fontSize = scaledSp(percentWidth = 0.04f, percentHeight = 0.03f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
