package com.mattmik.rapira.objects

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RapiraProcedureTest {

    @Test
    fun toStringReturnsUserFriendlyRepresentation() =
        Assertions.assertEquals("procedure", RapiraProcedure.toString())
}
