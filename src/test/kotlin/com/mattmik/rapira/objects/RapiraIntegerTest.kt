package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.checkAll
import io.kotest.property.forAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NewRapiraIntegerTest : StringSpec({
    "addition with integer returns integer" {
        forAll<Int, Int> { a, b ->
            RapiraInteger(a).add(RapiraInteger(b)) == RapiraInteger(a + b)
        }
    }

    "addition with real returns real" {
        forAll<Int, Double> { a, b ->
            RapiraInteger(a).add(RapiraReal(b)) == RapiraReal(a + b)
        }
    }

    "subtraction with integer returns integer" {
        forAll<Int, Int> { a, b ->
            RapiraInteger(a).subtract(RapiraInteger(b)) == RapiraInteger(a - b)
        }
    }

    "subtraction with real returns real" {
        forAll<Int, Double> { a, b ->
            RapiraInteger(a).subtract(RapiraReal(b)) == RapiraReal(a - b)
        }
    }

    "less than with integer returns logical" {
        forAll<Int, Int> { a, b ->
            RapiraInteger(a).lessThan(RapiraInteger(b)) == RapiraLogical(a < b)
        }
    }

    "less than with real returns logical" {
        forAll<Int, Double> { a, b ->
            RapiraInteger(a).lessThan(RapiraReal(b)) == RapiraLogical(a < b)
        }
    }

    "less than or equal to with integer returns logical" {
        forAll<Int, Int> { a, b ->
            RapiraInteger(a).lessThanEqualTo(RapiraInteger(b)) == RapiraLogical(a <= b)
        }
    }

    "less than or equal to with real returns logical" {
        forAll<Int, Double> { a, b ->
            RapiraInteger(a).lessThanEqualTo(RapiraReal(b)) == RapiraLogical(a <= b)
        }
    }

    "greater than with integer returns logical" {
        forAll<Int, Int> { a, b ->
            RapiraInteger(a).greaterThan(RapiraInteger(b)) == RapiraLogical(a > b)
        }
    }

    "greater than with real returns logical" {
        forAll<Int, Double> { a, b ->
            RapiraInteger(a).greaterThan(RapiraReal(b)) == RapiraLogical(a > b)
        }
    }

    "greater than or equal to with integer returns logical" {
        forAll<Int, Int> { a, b ->
            RapiraInteger(a).greaterThanEqualTo(RapiraInteger(b)) == RapiraLogical(a >= b)
        }
    }

    "greater than or equal to with real returns logical" {
        forAll<Int, Double> { a, b ->
            RapiraInteger(a).greaterThanEqualTo(RapiraReal(b)) == RapiraLogical(a >= b)
        }
    }

    // "modulus with integer returns integer" {
    //     forAll<Int, Int> { a, b ->
    //         RapiraInteger(a).modulus(RapiraInteger(b)) == RapiraInteger(a % b)
    //     }
    // }

    "toString returns user friendly representation" {
        checkAll<Int> { num -> RapiraInteger(num) shouldConvertToString "$num" }
    }
})

class RapiraIntegerTest {

    @Test
    fun lessThanWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).lessThan(it) }
    }

    @Test
    fun greaterThanWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).greaterThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).greaterThan(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).greaterThan(it) }
    }

    @Test
    fun lessThanEqualToWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).lessThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).lessThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).lessThanEqualTo(it) }
    }

    @Test
    fun greaterThanEqualToWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure,
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10).greaterThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0).greaterThanEqualTo(it) }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10).greaterThanEqualTo(it) }
    }
}
