package com.example.wizardquiz.domain

import kotlin.random.Random

class OptionGenerator(private val random: Random = Random.Default) {
    fun prepare(question: Question, requestedOptionCount: Int): PreparedQuestion {
        val minAllowed = 2
        val maxPossible = 1 + question.wrongAnswers.size
        val effective = requestedOptionCount.coerceIn(minAllowed, maxPossible)
        val downgraded = effective < requestedOptionCount

        val wrongs = question.wrongAnswers
            .filter { it.isNotBlank() }
            .distinct()
            .shuffled(random)
            .take(effective - 1)

        val options = (wrongs + question.correctAnswer)
            .distinct()
            .shuffled(random)

        return PreparedQuestion(
            question = question,
            options = options,
            effectiveChoiceCount = options.size,
            wasDowngraded = downgraded
        )
    }
}
