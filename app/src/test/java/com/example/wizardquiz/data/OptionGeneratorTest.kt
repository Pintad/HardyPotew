package com.example.wizardquiz.data

import com.example.wizardquiz.domain.Difficulty
import com.example.wizardquiz.domain.OptionGenerator
import com.example.wizardquiz.domain.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class OptionGeneratorTest {

    private val question = Question(
        id = "x",
        category = "Sorts",
        difficulty = Difficulty.MEDIUM,
        text = "Question",
        correctAnswer = "Bonne",
        wrongAnswers = listOf("M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10"),
        explanation = null
    )

    @Test
    fun `supports 2 4 8 12 choices when possible`() {
        val generator = OptionGenerator(Random(0))
        listOf(2, 4, 8).forEach { count ->
            val prepared = generator.prepare(question, count)
            assertEquals(count, prepared.options.size)
            assertTrue(prepared.options.contains("Bonne"))
            assertFalse(prepared.wasDowngraded)
        }

        val prepared12 = generator.prepare(question, 12)
        assertEquals(11, prepared12.options.size)
        assertTrue(prepared12.wasDowngraded)
    }

    @Test
    fun `downgrades when not enough wrong answers`() {
        val small = question.copy(wrongAnswers = listOf("M1", "M2"))
        val generator = OptionGenerator(Random(1))

        val prepared = generator.prepare(small, 8)

        assertEquals(3, prepared.options.size)
        assertTrue(prepared.wasDowngraded)
        assertTrue(prepared.options.contains("Bonne"))
    }
}
