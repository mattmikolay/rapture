package com.mattmik.rapira.antlr

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import org.junit.Assert.assertEquals

class RapiraLangParserTest {

    private fun parseNumber(input: String): RapiraLangParser.NumberContext {
        val lexer = RapiraLangLexer(CharStreams.fromString(input))
        return RapiraLangParser(CommonTokenStream(lexer)).number()
    }

    @Test
    fun numberParsesIntegers() {
        val expectedValues = arrayOf("123", "+123", "-123")
        expectedValues.forEach {
            val output = parseNumber(it)
            assertEquals(it, output.INT()?.text)
        }
    }

    @Test
    fun numberParsesRealNumbers() {
        val expectedValues = arrayOf(
            "1.4", "+1.4", "-1.4",
            "1e5", "+1e5", "+1e+5",
            "1e+5", "-1e5", "-1e+5",
            "1e-5", "+1e-5", "-1e-5",
            "1.4e5", "+1.4e5", "+1.4e+5",
            "1.4e+5", "-1.4e5", "-1.4e+5",
            "1.4e-5", "+1.4e-5", "-1.4e-5"
        )
        expectedValues.forEach {
            val output = parseNumber(it)
            assertEquals(it, output.REAL()?.text)
        }
    }
}
