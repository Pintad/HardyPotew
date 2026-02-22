package com.example.wizardquiz.domain

object ScoringPolicy {
    private val choiceMultiplier = mapOf(
        2 to 4,
        4 to 3,
        8 to 2
    )

    fun pointsForCorrectAnswer(difficulty: Difficulty, effectiveChoiceCount: Int): Int {
        val multiplier = choiceMultiplier.entries
            .sortedBy { it.key }
            .firstOrNull { effectiveChoiceCount <= it.key }
            ?.value
            ?: 1
        return difficulty.bonus * multiplier
    }
}
