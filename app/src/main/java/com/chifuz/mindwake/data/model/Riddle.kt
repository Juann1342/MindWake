package com.chifuz.mindwake.data.model

import androidx.annotation.Keep
import com.chifuz.mindwake.data.repository.RiddleType

@Keep
data class Riddle(
    val id: Int,
    val question: String,
    val hints: List<String>,
    val answer: String,
    val type: RiddleType
)
