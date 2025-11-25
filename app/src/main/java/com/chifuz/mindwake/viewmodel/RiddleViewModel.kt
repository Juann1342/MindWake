package com.chifuz.mindwake.viewmodel

import ProgressDataStore
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifuz.mindwake.data.model.Riddle
import com.chifuz.mindwake.data.model.RiddleUiState
import com.chifuz.mindwake.data.repository.RiddleRepository
import com.chifuz.mindwake.data.repository.RiddleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RiddleViewModel(context: Context) : ViewModel() {

    private val repository = RiddleRepository(context)
    private val dataStore = ProgressDataStore(context)
    private val allRiddles: List<Riddle> = repository.loadAll()

    // Filtramos las listas por tipo para manejarlas independientemente
    private val riddleList = allRiddles.filter { it.type == RiddleType.RIDDLE }
    private val lateralList = allRiddles.filter { it.type == RiddleType.LATERAL }

    // Índices actuales de lectura
    private var nextRiddleIndex = 0
    private var nextLateralIndex = 0

    // Controla el ciclo: 0 (Acertijo), 1 (Acertijo), 2 (Lateral)
    private var cycleStep = 0

    private val _uiState = MutableStateFlow<RiddleUiState?>(null)
    val uiState: StateFlow<RiddleUiState?> = _uiState

    private val _progressFlow = MutableStateFlow(0f)
    val progressFlow: StateFlow<Float> = _progressFlow

    // Estado para controlar el diálogo de bienvenida
    private val _showWelcomeDialog = MutableStateFlow(false)
    val showWelcomeDialog: StateFlow<Boolean> = _showWelcomeDialog

    init {
        viewModelScope.launch {
            // Recuperamos el progreso guardado asegurando que esté dentro de los límites válidos
            nextRiddleIndex = dataStore.getNextRiddleIndex().coerceIn(0, riddleList.lastIndex)
            nextLateralIndex = dataStore.getNextLateralIndex().coerceIn(0, lateralList.lastIndex)
            cycleStep = dataStore.getCycleStep().coerceIn(0, 2)

            // Si es la primera vez absoluta, comenzamos en el paso 0
            if (nextRiddleIndex == 0 && nextLateralIndex == 0) {
                cycleStep = 0
            }

            // Verificar si debemos mostrar el tutorial (solo si nunca se mostró)
            if (!dataStore.isTutorialShown()) {
                _showWelcomeDialog.value = true
            }

            // Actualizamos la barra de progreso visual
            updateProgressFlow()

            // Cargamos el primer elemento sin avanzar índices (carga inicial)
            loadNext(initialLoad = true)
        }
    }

    // Acción al cerrar el tutorial
    fun dismissWelcomeDialog() {
        _showWelcomeDialog.value = false
        viewModelScope.launch {
            dataStore.setTutorialShown()
        }
    }

    // ✅ NUEVO: Acción para mostrar el tutorial manualmente (botón de ayuda)
    fun showWelcomeDialog() {
        _showWelcomeDialog.value = true
    }

    // Función principal llamada por la UI para cargar contenido
    fun loadNext(initialLoad: Boolean = false) {
        val riddleToShow = if (initialLoad) {
            // Si es carga inicial, simplemente obtenemos el acertijo actual sin modificar índices
            getCurrentRiddleBasedOnState()
        } else {
            // Si es acción del usuario, avanzamos la secuencia, guardamos y devolvemos el nuevo acertijo
            advanceSequence()
            getCurrentRiddleBasedOnState()
        }

        // Actualizamos el estado de la UI con el nuevo acertijo y reiniciamos pistas/respuesta
        _uiState.value = RiddleUiState(riddle = riddleToShow)
        updateProgressFlow()
    }

    // Determina qué objeto Riddle devolver basándose en los índices y el paso del ciclo actual
    private fun getCurrentRiddleBasedOnState(): Riddle {
        return when (cycleStep) {
            0, 1 -> {
                // Aseguramos que el índice sea seguro con modulo
                val safeIndex = nextRiddleIndex % riddleList.size
                riddleList[safeIndex]
            }
            2 -> {
                val safeIndex = nextLateralIndex % lateralList.size
                lateralList[safeIndex]
            }
            else -> riddleList[0] // Fallback por seguridad
        }
    }

    // Lógica matemática para calcular los siguientes índices y persistirlos
    private fun advanceSequence() {
        when (cycleStep) {
            0 -> {
                // Fin del primer acertijo del ciclo. Avanzamos índice y pasamos al paso 1
                nextRiddleIndex = (nextRiddleIndex + 1) % riddleList.size
                cycleStep = 1
            }
            1 -> {
                // Fin del segundo acertijo del ciclo. Avanzamos índice y pasamos al paso 2 (Lateral)
                nextRiddleIndex = (nextRiddleIndex + 1) % riddleList.size
                cycleStep = 2
            }
            2 -> {
                // Fin del lateral. Avanzamos índice de laterales y reiniciamos el ciclo a 0 (Acertijos)
                // Nota: No tocamos nextRiddleIndex aquí, ya se actualizó en los pasos anteriores.
                nextLateralIndex = (nextLateralIndex + 1) % lateralList.size
                cycleStep = 0
            }
        }
        persistProgress()
    }

    // Guarda los índices actuales en DataStore
    private fun persistProgress() {
        viewModelScope.launch {
            dataStore.saveIndexes(nextRiddleIndex, nextLateralIndex, cycleStep)
        }
    }

    // Calcula el progreso visual basado en el paso del ciclo (1/3, 2/3, 3/3)
    private fun updateProgressFlow() {
        _progressFlow.value = when (cycleStep) {
            0 -> 0.33f
            1 -> 0.66f
            2 -> 1f
            else -> 0f
        }
    }

    // Muestra la siguiente pista disponible en la UI
    fun showNextHint() {
        _uiState.update { state ->
            state?.let {
                if (it.hintIndex < it.riddle.hints.size)
                    it.copy(hintIndex = it.hintIndex + 1)
                else it
            }
        }
    }

    // Revela la respuesta en la UI
    fun showAnswer() {
        _uiState.update { it?.copy(isAnswerShown = true) }
    }
}