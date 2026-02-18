package com.example.wizardquiz.data

import com.example.wizardquiz.domain.Difficulty
import com.example.wizardquiz.domain.Question

class CsvQuestionParser {

    fun parse(csvText: String): List<Question> {
        val cleaned = csvText.removePrefix("\uFEFF")
        if (cleaned.isBlank()) return emptyList()

        val rows = parseRows(cleaned)
            .filter { row -> row.any { it.isNotBlank() } }
        if (rows.isEmpty()) return emptyList()

        val delimiter = detectDelimiter(rows.first())
        val parsedRows = parseRows(cleaned, delimiter)
            .filter { row -> row.any { it.isNotBlank() } }
        if (parsedRows.size <= 1) return emptyList()

        return parsedRows.drop(1)
            .mapNotNull { row -> row.toQuestionOrNull() }
    }

    private fun detectDelimiter(headerFieldsGuess: List<String>): Char {
        val header = headerFieldsGuess.joinToString("")
        val semicolonScore = header.count { it == ';' }
        val commaScore = header.count { it == ',' }
        return if (semicolonScore >= commaScore) ';' else ','
    }

    private fun parseRows(input: String, delimiter: Char = ';'): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val currentRow = mutableListOf<String>()
        val currentField = StringBuilder()

        var inQuotes = false
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < input.length && input[i + 1] == '"') {
                        currentField.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == delimiter && !inQuotes -> {
                    currentRow += currentField.toString().trim()
                    currentField.clear()
                }
                (c == '\n' || c == '\r') && !inQuotes -> {
                    if (c == '\r' && i + 1 < input.length && input[i + 1] == '\n') {
                        i++
                    }
                    currentRow += currentField.toString().trim()
                    currentField.clear()
                    rows += currentRow.toList()
                    currentRow.clear()
                }
                else -> currentField.append(c)
            }
            i++
        }

        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            currentRow += currentField.toString().trim()
            rows += currentRow.toList()
        }

        return rows
    }

    private fun List<String>.toQuestionOrNull(): Question? {
        fun get(index: Int): String = this.getOrNull(index).orEmpty().trim()

        val id = get(0)
        val category = get(1)
        val difficulty = Difficulty.fromRaw(get(2))
        val question = get(3)
        val correct = get(4)

        if (id.isBlank() || question.isBlank() || correct.isBlank()) return null

        val wrongAnswers = (5..14)
            .map(::get)
            .filter { it.isNotBlank() }
            .distinct()
        val explanation = get(15).ifBlank { null }

        return Question(
            id = id,
            category = category.ifBlank { "General" },
            difficulty = difficulty,
            text = question,
            correctAnswer = correct,
            wrongAnswers = wrongAnswers,
            explanation = explanation
        )
    }
}
