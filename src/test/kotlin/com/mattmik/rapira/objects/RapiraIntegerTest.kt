package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class RapiraIntegerTest {

    private val trueLogical = RapiraLogical(true)
    private val falseLogical = RapiraLogical(false)

    @TestFactory
    fun lessThanWithNumberReturnsLogical() = makeObjectOperationTests(
        "<",
        { a: RapiraObject, b: RapiraObject -> a.lessThan(b) },

        // Integer
        Triple(RapiraInteger(0), RapiraInteger(10), trueLogical),
        Triple(RapiraInteger(10), RapiraInteger(0), falseLogical),
        Triple(RapiraInteger(10), RapiraInteger(10), falseLogical),

        // Real
        Triple(RapiraInteger(0), RapiraReal(10.0), trueLogical),
        Triple(RapiraInteger(10), RapiraReal(0.0), falseLogical),
        Triple(RapiraInteger(10), RapiraReal(10.0), falseLogical)
    )

    @Test
    fun lessThanWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction,
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).lessThan(it) }
    }

    @TestFactory
    fun greaterThanWithNumberReturnsLogical() = makeObjectOperationTests(
        ">",
        { a: RapiraObject, b: RapiraObject -> a.greaterThan(b) },

        // Integer
        Triple(RapiraInteger(0), RapiraInteger(10), falseLogical),
        Triple(RapiraInteger(10), RapiraInteger(0), trueLogical),
        Triple(RapiraInteger(10), RapiraInteger(10), falseLogical),

        // Real
        Triple(RapiraInteger(0), RapiraReal(10.0), falseLogical),
        Triple(RapiraInteger(10), RapiraReal(0.0), trueLogical),
        Triple(RapiraInteger(10), RapiraReal(10.0), falseLogical)
    )

    @Test
    fun greaterThanWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction,
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).greaterThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).greaterThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).greaterThan(it) }
    }

    @TestFactory
    fun lessThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        "<=",
        { a: RapiraObject, b: RapiraObject -> a.lessThanEqualTo(b) },

        // Integer
        Triple(RapiraInteger(0), RapiraInteger(10), trueLogical),
        Triple(RapiraInteger(10), RapiraInteger(0), falseLogical),
        Triple(RapiraInteger(10), RapiraInteger(10), trueLogical),

        // Real
        Triple(RapiraInteger(0), RapiraReal(10.0), trueLogical),
        Triple(RapiraInteger(10), RapiraReal(0.0), falseLogical),
        Triple(RapiraInteger(10), RapiraReal(10.0), trueLogical)
    )

    @Test
    fun lessThanEqualToWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction,
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).lessThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).lessThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).lessThanEqualTo(it) }
    }

    @TestFactory
    fun greaterThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        ">=",
        { a: RapiraObject, b: RapiraObject -> a.greaterThanEqualTo(b) },

        // Integer
        Triple(RapiraInteger(0), RapiraInteger(10), falseLogical),
        Triple(RapiraInteger(10), RapiraInteger(0), trueLogical),
        Triple(RapiraInteger(10), RapiraInteger(10), trueLogical),

        // Real
        Triple(RapiraInteger(0), RapiraReal(10.0), falseLogical),
        Triple(RapiraInteger(10), RapiraReal(0.0), trueLogical),
        Triple(RapiraInteger(10), RapiraReal(10.0), trueLogical)
    )

    @Test
    fun greaterThanEqualToWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction,
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).greaterThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).greaterThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).greaterThanEqualTo(it) }
    }

    @Test
    fun toStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("0", RapiraInteger(0).toString())
        Assertions.assertEquals("123", RapiraInteger(123).toString())
        Assertions.assertEquals("-123", RapiraInteger(-123).toString())
    }
}
