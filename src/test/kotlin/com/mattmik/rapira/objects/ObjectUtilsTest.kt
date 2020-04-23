package com.mattmik.rapira.objects

import org.junit.jupiter.api.Assertions
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

    @Test
    fun formatRapiraObjectReturnsCorrectRepresentations() {
        Assertions.assertEquals(
            "empty",
            formatRapiraObject(RapiraEmpty))
        Assertions.assertEquals(
            "procedure",
            formatRapiraObject(RapiraProcedure))
        Assertions.assertEquals(
            "function",
            formatRapiraObject(RapiraFunction))
        Assertions.assertEquals(
            "yes",
            formatRapiraObject(RapiraLogical(true)))
        Assertions.assertEquals(
            "no",
            formatRapiraObject(RapiraLogical(false)))
        Assertions.assertEquals(
            "123",
            formatRapiraObject(RapiraInteger(123)))
        Assertions.assertEquals(
            "1.4",
            formatRapiraObject(RapiraReal(1.4)))
        Assertions.assertEquals(
            "",
            formatRapiraObject(RapiraText("")))
        Assertions.assertEquals(
            "Hello!",
            formatRapiraObject(RapiraText("Hello!")))
        Assertions.assertEquals(
            "How about some \"double quotes\"?",
            formatRapiraObject(RapiraText("How about some \"double quotes\"?"))
        )
    }
}
