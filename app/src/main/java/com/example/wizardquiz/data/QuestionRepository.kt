package com.example.wizardquiz.data

import android.content.Context
import com.example.wizardquiz.domain.OptionGenerator
import com.example.wizardquiz.domain.PreparedQuestion
import com.example.wizardquiz.domain.Question
import kotlin.random.Random

class QuestionRepository(
    private val context: Context,
    private val parser: CsvQuestionParser = CsvQuestionParser(),
    private val random: Random = Random.Default,
    private val optionGenerator: OptionGenerator = OptionGenerator(random)
) {
    private var cache: List<Question>? = null

    fun loadQuestionsFromAssets(fileName: String = "questions.csv"): List<Question> {
        if (cache != null) return cache.orEmpty()
        val csv = context.assets.open(fileName).bufferedReader(Charsets.UTF_8).use { it.readText() }
        return parser.parse(csv).also { cache = it }
    }

    fun categories(): List<String> = loadQuestionsFromAssets()
        .map { it.category }
        .distinct()
        .sorted()

    fun selectQuizQuestions(count: Int = 10): List<Question> =
        loadQuestionsFromAssets().shuffled(random).take(count)

    fun selectTrainingQuestions(category: String?): List<Question> {
        val all = loadQuestionsFromAssets()
        val filtered = if (category.isNullOrBlank()) all else all.filter { it.category == category }
        return filtered.shuffled(random)
    }

    fun prepareOptions(question: Question, requestedOptionCount: Int): PreparedQuestion =
        optionGenerator.prepare(question, requestedOptionCount)
}
