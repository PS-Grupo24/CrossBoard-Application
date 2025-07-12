package com.crossBoard.repositoryTests
import com.crossBoard.repository.interfaces.generateTokenValue
import org.junit.jupiter.api.*
class GenerateTokenTests {

    @Test
    fun testGenerateToken() {
        val rubenToken = generateTokenValue()
        val luisToken = generateTokenValue()

        println(rubenToken)
        println(luisToken)
    }
}