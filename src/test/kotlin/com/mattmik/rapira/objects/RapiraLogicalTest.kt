package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RapiraLogicalTest {

    private val trueLogical = RapiraLogical(true)
    private val falseLogical = RapiraLogical(false)
    private val incompatibleObjects = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction,
        RapiraInteger(1),
        RapiraReal(1.0),
        RapiraText("hello"),
        RapiraSequence()
    )

    @Test
    fun andWithLogicalReturnsLogical() {
        assertEquals(
            trueLogical,
            trueLogical.and(trueLogical)
        )
        assertEquals(
            falseLogical,
            trueLogical.and(falseLogical)
        )
        assertEquals(
            falseLogical,
            falseLogical.and(trueLogical)
        )
        assertEquals(
            falseLogical,
            falseLogical.and(falseLogical)
        )
    }

    @Test
    fun andWithOtherTypesThrowsException() = incompatibleObjects.forEach {
        assertThrows<RapiraInvalidOperationError> { trueLogical.and(it) }
        assertThrows<RapiraInvalidOperationError> { falseLogical.and(it) }
    }

    @Test
    fun orWithLogicalReturnsLogical() {
        assertEquals(
            trueLogical,
            trueLogical.or(trueLogical)
        )
        assertEquals(
            trueLogical,
            trueLogical.or(falseLogical)
        )
        assertEquals(
            trueLogical,
            falseLogical.or(trueLogical)
        )
        assertEquals(
            falseLogical,
            falseLogical.or(falseLogical)
        )
    }

    @Test
    fun orWithOtherTypesThrowsException() = incompatibleObjects.forEach {
        assertThrows<RapiraInvalidOperationError> { trueLogical.or(it) }
        assertThrows<RapiraInvalidOperationError> { falseLogical.or(it) }
    }

    @Test
    fun notReturnsOppositeLogical() {
        assertEquals(trueLogical, falseLogical.not())
        assertEquals(falseLogical, trueLogical.not())
    }

    @Test
    fun toStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("yes", trueLogical.toString())
        Assertions.assertEquals("no", falseLogical.toString())
    }
}
