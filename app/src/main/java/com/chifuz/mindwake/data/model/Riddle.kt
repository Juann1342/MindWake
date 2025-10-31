package com.chifuz.mindwake.data.model

import com.chifuz.mindwake.data.repository.RiddleType

data class Riddle(
    val id: Int,
    val question: String,
    val hints: List<String>,
    val answer: String,
    val type: RiddleType
)
