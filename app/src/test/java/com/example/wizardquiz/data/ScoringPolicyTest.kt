package com.example.wizardquiz.data

import com.example.wizardquiz.domain.Difficulty
import com.example.wizardquiz.domain.ScoringPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class ScoringPolicyTest {

    @Test
    fun `awards more points when fewer choices`() {
        val easy2 = ScoringPolicy.pointsForCorrectAnswer(Difficulty.EASY, 2)
        val easy12 = ScoringPolicy.pointsForCorrectAnswer(Difficulty.EASY, 12)
        assertEquals(4, easy2)
        assertEquals(1, easy12)
    }

    @Test
    fun `difficulty is multiplied`() {
        val hard4 = ScoringPolicy.pointsForCorrectAnswer(Difficulty.HARD, 4)
        assertEquals(9, hard4)
    }
}
