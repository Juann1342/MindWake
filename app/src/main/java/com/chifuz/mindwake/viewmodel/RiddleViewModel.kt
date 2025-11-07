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

    private val riddleList = allRiddles.filter { it.type == RiddleType.RIDDLE }
    private val lateralList = allRiddles.filter { it.type == RiddleType.LATERAL }

    private var nextRiddleIndex = 0
    private var nextLateralIndex = 0
    private var cycleStep = 0 // 0→riddle1, 1→riddle2, 2→lateral

    private val _uiState = MutableStateFlow<RiddleUiState?>(null)
    val uiState: StateFlow<RiddleUiState?> = _uiState

    // Inicializamos en 0, luego updateProgressFlow ajusta
    private val _progressFlow = MutableStateFlow(0f)
    val progressFlow: StateFlow<Float> = _progressFlow

    init {
        viewModelScope.launch {
            nextRiddleIndex = dataStore.getNextRiddleIndex().coerceIn(0, riddleList.lastIndex)
            nextLateralIndex = dataStore.getNextLateralIndex().coerceIn(0, lateralList.lastIndex)
            cycleStep = dataStore.getCycleStep().coerceIn(0, 2)

            // Primera vez que abre la app
            if (nextRiddleIndex == 0 && nextLateralIndex == 0) {
                cycleStep = 0
            }

            updateProgressFlow()
            loadNext(initialLoad = true)
        }
    }

    fun loadNext(initialLoad: Boolean = false) {
        val riddleToShow = when {
            initialLoad -> {
                // Primer acertijo: lo mostramos, pero adelantamos índices para que siguiente click funcione
                val r = riddleList[nextRiddleIndex]
                nextRiddleIndex = (nextRiddleIndex + 1) % riddleList.size
                cycleStep = 1 // pasamos al segundo acertijo del ciclo
                persistProgress()
                r
            }
            else -> getNextInSequence(initialLoad = false)
        }

        _uiState.value = RiddleUiState(riddle = riddleToShow)
        updateProgressFlow()
    }



    private fun getNextInSequence(initialLoad: Boolean = false): Riddle {
        return when (cycleStep) {
            0, 1 -> { // mostrar acertijos
                val r = riddleList[nextRiddleIndex]

                if (!initialLoad) {
                    nextRiddleIndex = (nextRiddleIndex + 1) % riddleList.size
                    cycleStep++
                    persistProgress()
                }

                r
            }

            2 -> { // mostrar lateral
                val r = lateralList[nextLateralIndex]

                if (!initialLoad) {
                    nextLateralIndex = (nextLateralIndex + 1) % lateralList.size

                    // Después del lateral, avanzar a los 2 acertijos siguientes
                    nextRiddleIndex = (nextRiddleIndex + 2) % riddleList.size

                    cycleStep = 0
                    persistProgress()
                }

                r
            }

            else -> {
                val r = riddleList[nextRiddleIndex]
                r
            }
        }
    }

    private fun persistProgress() {
        viewModelScope.launch {
            dataStore.saveIndexes(nextRiddleIndex, nextLateralIndex, cycleStep)
        }
    }

    private fun updateProgressFlow() {
        _progressFlow.value = when (cycleStep) {
            0 -> 1f
            1 -> 0.3f
            2 -> 0.6f
            else -> 0.3f
        }
    }

    fun showNextHint() {
        _uiState.update { state ->
            state?.let {
                if (it.hintIndex < it.riddle.hints.size)
                    it.copy(hintIndex = it.hintIndex + 1)
                else it
            }
        }
    }

    fun showAnswer() {
        _uiState.update { it?.copy(isAnswerShown = true) }
    }

    fun debugState(): String {
        return "nextRiddleIndex=$nextRiddleIndex nextLateralIndex=$nextLateralIndex cycleStep=$cycleStep"
    }


}
