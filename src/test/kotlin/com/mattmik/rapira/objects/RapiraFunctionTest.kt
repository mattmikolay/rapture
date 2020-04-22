package com.mattmik.rapira.objects

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RapiraFunctionTest {

    @Test
    fun toStringReturnsUserFriendlyRepresentation() =
        Assertions.assertEquals("function", RapiraFunction.toString())
}
