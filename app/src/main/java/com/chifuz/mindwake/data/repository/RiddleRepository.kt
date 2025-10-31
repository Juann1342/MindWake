package com.chifuz.mindwake.data.repository

import android.content.Context
import com.chifuz.mindwake.data.model.Riddle



class RiddleRepository(private val context: Context) {

    fun loadAll(): List<Riddle> {
        val riddles = mutableListOf<Riddle>()

        // 🔹 10 acertijos
        for (i in 1..10) {
            riddles.add(loadItem(i, RiddleType.RIDDLE))
        }

        // 🔸 5 de pensamiento lateral
        for (i in 1..5) {
            riddles.add(loadItem(i + 100, RiddleType.LATERAL))
        }

        return riddles
    }

    private fun loadItem(id: Int, type: RiddleType): Riddle {
        val prefix = if (type == RiddleType.RIDDLE) "riddle" else "lateral"
        val res = context.resources
        val index = if (type == RiddleType.RIDDLE) id else id - 100

        val question = res.getString(res.getIdentifier("${prefix}_${index}_text", "string", context.packageName))
        val hint1 = res.getString(res.getIdentifier("${prefix}_${index}_hint_1", "string", context.packageName))
        val hint2 = res.getString(res.getIdentifier("${prefix}_${index}_hint_2", "string", context.packageName))
        val hint3 = res.getString(res.getIdentifier("${prefix}_${index}_hint_3", "string", context.packageName))
        val answer = res.getString(res.getIdentifier("${prefix}_${index}_answer", "string", context.packageName))

        return Riddle(
            id = id,
            question = question,
            hints = listOf(hint1, hint2, hint3),
            answer = answer,
            type = type
        )
    }
}
