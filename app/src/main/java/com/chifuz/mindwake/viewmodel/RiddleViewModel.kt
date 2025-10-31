package com.chifuz.mindwake.viewmodel

import ProgressDataStore
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifuz.mindwake.data.model.Riddle
import com.chifuz.mindwake.data.model.RiddleUiState
import com.chifuz.mindwake.data.repository.RiddleRepository
import com.chifuz.mindwake.data.repository.RiddleType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class RiddleViewModel(context: Context) : ViewModel() {

    private val repository = RiddleRepository(context)
    private val dataStore = ProgressDataStore(context)

    private val riddles: List<Riddle> = repository.loadAll()

    private var currentIndex = 0 // índice global en la lista completa
    private var cycleIndex = 0 // índice dentro de la secuencia 2R + 1L

    private val _uiState = MutableStateFlow<RiddleUiState?>(null)
    val uiState: StateFlow<RiddleUiState?> = _uiState

    init {
        viewModelScope.launch {
            // Recuperamos progreso guardado
            currentIndex = dataStore.getLastIndex()
            cycleIndex = dataStore.getCycleIndex()
            loadNext(initialLoad = true)
        }
    }

    fun loadNext(initialLoad: Boolean = false) {
        val next = getNextInSequence(initialLoad)
        _uiState.value = RiddleUiState(riddle = next)
    }

    private fun getNextInSequence(initialLoad: Boolean = false): Riddle {
        val expectedType = if (cycleIndex % 3 < 2) RiddleType.RIDDLE else RiddleType.LATERAL

        val next = if (initialLoad) {
            // Al iniciar, buscamos el primer acertijo o lateral que corresponda según cycleIndex
            riddles.drop(currentIndex).firstOrNull { it.type == expectedType }
                ?: riddles.first { it.type == expectedType }
        } else {
            // Secuencia normal
            riddles.drop(currentIndex).firstOrNull { it.type == expectedType }
                ?: riddles.first { it.type == expectedType }
        }

        // Actualizamos índices
        currentIndex = (riddles.indexOf(next) + 1) % riddles.size
        cycleIndex = (cycleIndex + 1) % 3

        // Guardamos progreso
        viewModelScope.launch {
            dataStore.saveIndexes(currentIndex, cycleIndex)
        }

        return next
    }

    fun showNextHint() {
        _uiState.update { state ->
            state?.let {
                if (it.hintIndex < it.riddle.hints.size) it.copy(hintIndex = it.hintIndex + 1) else it
            }
        }
    }

    fun showAnswer() {
        _uiState.update { state -> state?.copy(isAnswerShown = true) }
    }

    fun isLastLateral(): Boolean {
        val state = _uiState.value ?: return false
        return state.riddle.type == RiddleType.LATERAL && cycleIndex == 0
    }
}
