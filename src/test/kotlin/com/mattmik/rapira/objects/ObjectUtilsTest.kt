package com.mattmik.rapira.objects

import org.junit.jupiter.api.Test

class ObjectUtilsTest {

    @Test
    fun parseEscapedTextHandlesDoubleQuotes() {
        assertEquals(
            RapiraText(""),
            parseEscapedText("\"\"")
        )
        assertEquals(
            RapiraText("Test!"),
            parseEscapedText("\"Test!\"")
        )
        assertEquals(
            RapiraText("How about some \"double quotes\"?"),
            parseEscapedText("\"How about some \"\"double quotes\"\"?\"")
        )
    }
}