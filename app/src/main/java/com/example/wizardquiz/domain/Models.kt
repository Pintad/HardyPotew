package com.example.wizardquiz.domain

enum class Difficulty(val bonus: Int) {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    companion object {
        fun fromRaw(raw: String): Difficulty {
            return entries.firstOrNull { it.name.equals(raw.trim(), ignoreCase = true) } ?: EASY
        }
    }
}

data class Question(
    val id: String,
    val category: String,
    val difficulty: Difficulty,
    val text: String,
    val correctAnswer: String,
    val wrongAnswers: List<String>,
    val explanation: String?
)

enum class QuizMode { QUIZ, TRAINING }

data class PreparedQuestion(
    val question: Question,
    val options: List<String>,
    val effectiveChoiceCount: Int,
    val wasDowngraded: Boolean
)
