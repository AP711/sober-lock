package com.ap711.soberlock

import java.security.SecureRandom

data class Puzzle(val question: String, val answer: Int)

class PuzzleGenerator {
    private val secureRandom = SecureRandom()
    
    fun generatePuzzle(difficulty: Int = 1): Puzzle {
        val validatedDifficulty = when {
            difficulty < 1 -> 1
            difficulty > 3 -> 3
            else -> difficulty
        }
        
        return when (validatedDifficulty) {
            1 -> {
                val a = secureRandom.nextInt(9) + 1
                val b = secureRandom.nextInt(9) + 1
                Puzzle("What is $a + $b?", a + b)
            }
            2 -> {
                val a = secureRandom.nextInt(40) + 10
                val b = secureRandom.nextInt(19) + 1
                Puzzle("What is $a - $b?", a - b)
            }
            else -> {
                val a = secureRandom.nextInt(10) + 2
                val b = secureRandom.nextInt(10) + 2
                Puzzle("What is $a × $b?", a * b)
            }
        }
    }
}