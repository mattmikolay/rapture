package com.mattmik.rapira.objects

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RapiraEmptyTest {

    @Test
    fun toStringReturnsUserFriendlyRepresentation() =
        Assertions.assertEquals("empty", RapiraEmpty.toString())
}
