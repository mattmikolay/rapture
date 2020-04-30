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
            a.toRInteger() + b.toRInteger() shouldBe (a + b).toRInteger()
        }
    }

    "addition with real returns real" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() + b.toRReal() shouldBe (a + b).toRReal()
        }
    }

    "subtraction with integer returns integer" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() - b.toRInteger() shouldBe (a - b).toRInteger()
        }
    }

    "subtraction with real returns real" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() - b.toRReal() shouldBe (a - b).toRReal()
        }
    }

    "less than with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() lessThan b.toRInteger() shouldBe RLogical(a < b)
        }
    }

    "less than with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() lessThan b.toRReal() shouldBe RLogical(a < b)
        }
    }

    "less than or equal to with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() lessThanEqualTo b.toRInteger() shouldBe RLogical(a <= b)
        }
    }

    "less than or equal to with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() lessThanEqualTo b.toRReal() shouldBe RLogical(a <= b)
        }
    }

    "greater than with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() greaterThan b.toRInteger() shouldBe RLogical(a > b)
        }
    }

    "greater than with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() greaterThan b.toRReal() shouldBe RLogical(a > b)
        }
    }

    "greater than or equal to with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() greaterThanEqualTo b.toRInteger() shouldBe RLogical(a >= b)
        }
    }

    "greater than or equal to with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() greaterThanEqualTo b.toRReal() shouldBe RLogical(a >= b)
        }
    }

    "negate returns integer" {
        checkAll<Int> {
            num -> num.toRInteger().negate() shouldBe (-num).toRInteger()
        }
    }

    // "modulus with integer returns integer" {
    //     forAll<Int, Int> { a, b ->
    //         RapiraInteger(a).modulus(b.toRapiraInteger()) == RapiraInteger(a % b)
    //     }
    // }

    "toString returns user friendly representation" {
        checkAll<Int> { num -> RInteger(num) shouldConvertToString "$num" }
    }
})

class RIntegerTest {

    @Test
    fun lessThanWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RInteger(0).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10).lessThan(it) }
    }

    @Test
    fun greaterThanWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RInteger(0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10) greaterThan it }
    }

    @Test
    fun lessThanEqualToWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10) lessThanEqualTo it }
    }

    @Test
    fun greaterThanEqualToWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10) greaterThanEqualTo it }
    }
}
