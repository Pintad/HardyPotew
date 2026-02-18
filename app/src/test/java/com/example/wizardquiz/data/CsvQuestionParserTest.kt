package com.example.wizardquiz.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvQuestionParserTest {

    private val parser = CsvQuestionParser()

    @Test
    fun `parse handles BOM quotes delimiter and empty lines`() {
        val csv = "\uFEFFid;category;difficulty;question;correct;wrong1;wrong2;wrong3;wrong4;wrong5;wrong6;wrong7;wrong8;wrong9;wrong10;explanation\n" +
            "1;Sorts;easy;\"Quel sort produit une \"\"lueur\"\" stable ?\";Lumis;Ferro;Nodus;;;;;;;;;\"Texte; avec; points-virgules\"\n\n"

        val questions = parser.parse(csv)

        assertEquals(1, questions.size)
        assertEquals("1", questions.first().id)
        assertEquals("Quel sort produit une \"lueur\" stable ?", questions.first().text)
        assertEquals("Texte; avec; points-virgules", questions.first().explanation)
    }

    @Test
    fun `parse comma separated csv`() {
        val csv = "id,category,difficulty,question,correct,wrong1,wrong2,wrong3,wrong4,wrong5,wrong6,wrong7,wrong8,wrong9,wrong10,explanation\n" +
            "2,Potions,medium,Question ?,Bonne,M1,M2,,,,,,,,,Exp"

        val questions = parser.parse(csv)

        assertEquals(1, questions.size)
        assertEquals("Bonne", questions.first().correctAnswer)
        assertTrue(questions.first().wrongAnswers.contains("M1"))
    }
}
