package com.ap711.soberlock

import org.junit.Test
import org.junit.Assert.*

class PuzzleGeneratorTest {

    @Test
    fun addition_puzzle_isCorrect() {
        val generator = PuzzleGenerator()
        val puzzle = generator.generatePuzzle(1)
        assertTrue(puzzle.answer > 0)
        assertTrue(puzzle.question.contains("+"))
    }

    @Test
    fun subtraction_puzzle_isCorrect() {
        val generator = PuzzleGenerator()
        val puzzle = generator.generatePuzzle(2)
        assertTrue(puzzle.question.contains("-"))
    }

    @Test
    fun multiplication_puzzle_isCorrect() {
        val generator = PuzzleGenerator()
        val puzzle = generator.generatePuzzle(3)
        assertTrue(puzzle.question.contains("×"))
    }
}