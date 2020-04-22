package com.mattmik.rapira.objects

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RapiraTextTest {

    @Test
    fun toStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("\"Hello, world!\"", RapiraText("Hello, world!").toString())
        Assertions.assertEquals(
            "\"How about some \"double quotes\"? Fancy, eh?\"",
            RapiraText("How about some \"\"double quotes\"\"? Fancy, eh?").toString()
        )
    }
}
