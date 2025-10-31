package com.chifuz.mindwake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.chifuz.mindwake.data.model.RiddleViewModelFactory
import com.chifuz.mindwake.ui.navigation.MindWakeNavGraph
import com.chifuz.mindwake.viewmodel.RiddleViewModel
import com.chifuz.mindwake.ui.theme.MindWakeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindWakeTheme {
                val navController = rememberNavController() //Crea un controlador de navegación, para moverse entre pantallas.
                val factory = RiddleViewModelFactory(this)
                val viewModel: RiddleViewModel = viewModel(factory = factory)//Crea (o recupera) tu ViewModel, que guarda la lógica y el estado de la app.
                MindWakeNavGraph(navController, viewModel) //mapa de pantallas
            }
        }
    }
}
