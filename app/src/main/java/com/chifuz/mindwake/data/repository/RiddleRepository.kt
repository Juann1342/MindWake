package com.chifuz.mindwake.data.repository

import android.content.Context
import com.chifuz.mindwake.data.model.Riddle

class RiddleRepository(private val context: Context) {

    fun loadAll(): List<Riddle> {
        val res = context.resources
        val pkg = context.packageName

        val riddleQuestions = res.getStringArray(res.getIdentifier("riddle_text", "array", pkg))
        val riddleHint1 = res.getStringArray(res.getIdentifier("riddle_hints_1", "array", pkg))
        val riddleHint2 = res.getStringArray(res.getIdentifier("riddle_hints_2", "array", pkg))
        val riddleHint3 = res.getStringArray(res.getIdentifier("riddle_hints_3", "array", pkg))
        val riddleAnswers = res.getStringArray(res.getIdentifier("riddle_answers", "array", pkg))

        val lateralQuestions = res.getStringArray(res.getIdentifier("lateral_text", "array", pkg))
        val lateralHint1 = res.getStringArray(res.getIdentifier("lateral_hints_1", "array", pkg))
        val lateralHint2 = res.getStringArray(res.getIdentifier("lateral_hints_2", "array", pkg))
        val lateralHint3 = res.getStringArray(res.getIdentifier("lateral_hints_3", "array", pkg))
        val lateralAnswers = res.getStringArray(res.getIdentifier("lateral_answers", "array", pkg))


        val riddles = mutableListOf<Riddle>()

        //  Cargar acertijos
        for (i in riddleQuestions.indices) {
            riddles.add(
                Riddle(
                    id = i + 1,
                    question = riddleQuestions[i],
                    hints = listOf(riddleHint1[i], riddleHint2[i], riddleHint3[i]),
                    answer = riddleAnswers[i],
                    type = RiddleType.RIDDLE
                )
            )
        }

        //  Cargar pensamiento lateral
        for (i in lateralQuestions.indices) {
            riddles.add(
                Riddle(
                    id = 100 + i + 1,
                    question = lateralQuestions[i],
                    hints = listOf(lateralHint1[i], lateralHint2[i], lateralHint3[i]),
                    answer = lateralAnswers[i],
                    type = RiddleType.LATERAL
                )
            )
        }

        return riddles
    }


}
