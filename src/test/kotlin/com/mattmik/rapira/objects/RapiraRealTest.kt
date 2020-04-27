package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class NewRapiraRealTest : StringSpec({
    "negate returns integer" {
        checkAll<Double> {
                num -> num.toRapiraReal().negate() shouldBe (-num).toRapiraReal()
        }
    }
})

class RapiraRealTest {

    private val trueLogical = RapiraLogical(true)
    private val falseLogical = RapiraLogical(false)

    @TestFactory
    fun lessThanWithNumberReturnsLogical() = makeObjectOperationTests(
        "<",
        { a: RapiraObject, b: RapiraObject -> a lessThan b },

        // Integer
        Triple(RapiraReal(0.0), RapiraInteger(10), trueLogical),
        Triple(RapiraReal(10.0), RapiraInteger(0), falseLogical),
        Triple(RapiraReal(10.0), RapiraInteger(10), falseLogical),

        // Real
        Triple(RapiraReal(0.0), RapiraReal(10.0), trueLogical),
        Triple(RapiraReal(10.0), RapiraReal(0.0), falseLogical),
        Triple(RapiraReal(10.0), RapiraReal(10.0), falseLogical)
    )

    @Test
    fun lessThanWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure(),
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraReal(10.0) lessThan it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(0.0) lessThan it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(-10.0) lessThan it }
    }

    @TestFactory
    fun greaterThanWithNumberReturnsLogical() = makeObjectOperationTests(
        ">",
        { a: RapiraObject, b: RapiraObject -> a greaterThan b },

        // Integer
        Triple(RapiraReal(0.0), RapiraInteger(10), falseLogical),
        Triple(RapiraReal(10.0), RapiraInteger(0), trueLogical),
        Triple(RapiraReal(10.0), RapiraInteger(10), falseLogical),

        // Real
        Triple(RapiraReal(0.0), RapiraReal(10.0), falseLogical),
        Triple(RapiraReal(10.0), RapiraReal(0.0), trueLogical),
        Triple(RapiraReal(10.0), RapiraReal(10.0), falseLogical)
    )

    @Test
    fun greaterThanWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure(),
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraReal(10.0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(0.0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(-10.0) greaterThan it }
    }

    @TestFactory
    fun lessThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        "<=",
        { a: RapiraObject, b: RapiraObject -> a lessThanEqualTo b },

        // Integer
        Triple(RapiraReal(0.0), RapiraInteger(10), trueLogical),
        Triple(RapiraReal(10.0), RapiraInteger(0), falseLogical),
        Triple(RapiraReal(10.0), RapiraInteger(10), trueLogical),

        // Real
        Triple(RapiraReal(0.0), RapiraReal(10.0), trueLogical),
        Triple(RapiraReal(10.0), RapiraReal(0.0), falseLogical),
        Triple(RapiraReal(10.0), RapiraReal(10.0), trueLogical)
    )

    @Test
    fun lessThanEqualToWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure(),
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraReal(10.0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(0.0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(-10.0) lessThanEqualTo it }
    }

    @TestFactory
    fun greaterThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        ">=",
        { a: RapiraObject, b: RapiraObject -> a greaterThanEqualTo b },

        // Integer
        Triple(RapiraReal(0.0), RapiraInteger(10), falseLogical),
        Triple(RapiraReal(10.0), RapiraInteger(0), trueLogical),
        Triple(RapiraReal(10.0), RapiraInteger(10), trueLogical),

        // Real
        Triple(RapiraReal(0.0), RapiraReal(10.0), falseLogical),
        Triple(RapiraReal(10.0), RapiraReal(0.0), trueLogical),
        Triple(RapiraReal(10.0), RapiraReal(10.0), trueLogical)
    )

    @Test
    fun greaterThanEqualToWithOtherTypesThrowsException() = listOf(
        RapiraEmpty,
        RapiraProcedure(),
        RapiraFunction(),
        RapiraText("hello"),
        RapiraLogical(true),
        RapiraSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RapiraReal(10.0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(0.0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RapiraReal(-10.0) greaterThanEqualTo it }
    }

    @Test
    fun toStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("0.0", RapiraReal(0.0).toString())
        Assertions.assertEquals("1.23456789", RapiraReal(1.23456789).toString())
        Assertions.assertEquals("-1.23456789", RapiraReal(-1.23456789).toString())
    }
}
