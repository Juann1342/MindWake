package com.chifuz.mindwake.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🧠 MindWake",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onStartClick) {
                Text("Comenzar")
            }
        }
    }
}
