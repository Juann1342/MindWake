package com.chifuz.mindwake.data.model



import com.chifuz.mindwake.data.repository.RiddleType

data class RiddleUiState(
    val riddle: Riddle,
    val hintIndex: Int = 0,
    val isAnswerShown: Boolean = false
)
