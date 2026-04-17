package com.ap711.soberlock

import org.junit.Test
import org.junit.Assert.*

class ContactBlockerTest {

    @Test
    fun puzzleGenerator_addition_isCorrect() {
        val generator = PuzzleGenerator()
        val puzzle = generator.generatePuzzle(1)
        assertTrue("Answer should be positive", puzzle.answer > 0)
        assertTrue("Question should contain +", puzzle.question.contains("+"))
    }

    @Test
    fun puzzleGenerator_subtraction_isCorrect() {
        val generator = PuzzleGenerator()
        val puzzle = generator.generatePuzzle(2)
        assertTrue("Question should contain -", puzzle.question.contains("-"))
    }

    @Test
    fun puzzleGenerator_multiplication_isCorrect() {
        val generator = PuzzleGenerator()
        val puzzle = generator.generatePuzzle(3)
        assertTrue("Question should contain ×", puzzle.question.contains("×"))
    }
}