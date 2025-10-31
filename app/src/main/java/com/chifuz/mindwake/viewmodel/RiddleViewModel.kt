package com.chifuz.mindwake.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifuz.mindwake.data.datastore.ProgressDataStore
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

    private val riddles: List<Riddle> = repository.loadAll()
    private var currentIndex = 0
    private var sessionCounter = 0

    private val _uiState = MutableStateFlow<RiddleUiState?>(null)
    val uiState: StateFlow<RiddleUiState?> = _uiState

    init { loadNext() }

    fun loadNext() {
        if (riddles.isEmpty()) return

        val typeToShow = when (sessionCounter % 3) {
            0,1 -> RiddleType.RIDDLE
            else -> RiddleType.LATERAL
        }

        // Buscar próximo ejercicio del tipo adecuado
        val nextRiddle = riddles.drop(currentIndex).firstOrNull { it.type == typeToShow }
            ?: riddles.firstOrNull { it.type == typeToShow }!!

        currentIndex = (riddles.indexOf(nextRiddle) + 1) % riddles.size
        sessionCounter++

        _uiState.value = RiddleUiState(riddle = nextRiddle)
    }

    fun showNextHint() {
        _uiState.update { state ->
            state?.let {
                if (it.hintIndex < it.riddle.hints.size) it.copy(hintIndex = it.hintIndex + 1)
                else it
            }
        }
    }

    fun showAnswer() {
        _uiState.update { state -> state?.copy(isAnswerShown = true) }

        // Guardar progreso
        viewModelScope.launch {
            dataStore.incrementSolved()
        }
    }

    fun isLastLateral(): Boolean {
        val state = _uiState.value ?: return false
        return state.riddle.type == RiddleType.LATERAL && sessionCounter % 3 == 0
    }
}
