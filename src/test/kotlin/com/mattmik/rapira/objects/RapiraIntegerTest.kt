package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NewRapiraIntegerTest : StringSpec({
    "addition with integer returns integer" {
        checkAll<Int, Int> { a, b ->
            a.toRapiraInteger() + b.toRapiraInteger() shouldBe (a + b).toRapiraInteger()
        }
    }

    "addition with real returns real" {
        checkAll<Int, Double> { a, b ->
            a.toRapiraInteger() + b.toRapiraReal() shouldBe (a + b).toRapiraReal()
        }
    }

    "subtraction with integer returns integer" {
        checkAll<Int, Int> { a, b ->
            a.toRapiraInteger() - b.toRapiraInteger() shouldBe (a - b).toRapiraInteger()
        }
    }

    "subtraction with real returns real" {
        checkAll<Int, Double> { a, b ->
            a.toRapiraInteger() - b.toRapiraReal() shouldBe (a - b).toRapiraReal()
        }
    }

    "less than with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRapiraInteger() lessThan b.toRapiraInteger() shouldBe RapiraLogical(a < b)
        }
    }

    "less than with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRapiraInteger() lessThan b.toRapiraReal() shouldBe RapiraLogical(a < b)
        }
    }

    "less than or equal to with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRapiraInteger().lessThanEqualTo(b.toRapiraInteger()) shouldBe RapiraLogical(a <= b)
        }
    }

    "less than or equal to with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRapiraInteger().lessThanEqualTo(b.toRapiraReal()) shouldBe RapiraLogical(a <= b)
        }
    }

    "greater than with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRapiraInteger() greaterThan b.toRapiraInteger() shouldBe RapiraLogical(a > b)
        }
    }

    "greater than with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRapiraInteger() greaterThan b.toRapiraReal() shouldBe RapiraLogical(a > b)
        }
    }

    "greater than or equal to with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRapiraInteger().greaterThanEqualTo(b.toRapiraInteger()) shouldBe RapiraLogical(a >= b)
        }
    }

    "greater than or equal to with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRapiraInteger().greaterThanEqualTo(b.toRapiraReal()) shouldBe RapiraLogical(a >= b)
        }
    }

    "negate returns integer" {
        checkAll<Int> {
            num -> num.toRapiraInteger().negate() shouldBe (-num).toRapiraInteger()
        }
    }

    // "modulus with integer returns integer" {
    //     forAll<Int, Int> { a, b ->
    //         RapiraInteger(a).modulus(b.toRapiraInteger()) == RapiraInteger(a % b)
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
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(10) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RapiraInteger(-10) greaterThan it }
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
