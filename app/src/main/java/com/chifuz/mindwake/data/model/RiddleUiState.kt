package com.chifuz.mindwake.data.model

import androidx.annotation.Keep

@Keep
data class RiddleUiState(
    val riddle: Riddle,
    val hintIndex: Int = 0,
    val isAnswerShown: Boolean = false
)