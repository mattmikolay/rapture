package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class NewRRealTest : StringSpec({
    "negate returns integer" {
        checkAll<Double> {
                num -> num.toRReal().negate() shouldBe (-num).toRReal()
        }
    }
})

class RRealTest {

    private val trueLogical = RLogical(true)
    private val falseLogical = RLogical(false)

    @TestFactory
    fun lessThanWithNumberReturnsLogical() = makeObjectOperationTests(
        "<",
        { a: RObject, b: RObject -> a lessThan b },

        // Integer
        Triple(RReal(0.0), RInteger(10), trueLogical),
        Triple(RReal(10.0), RInteger(0), falseLogical),
        Triple(RReal(10.0), RInteger(10), falseLogical),

        // Real
        Triple(RReal(0.0), RReal(10.0), trueLogical),
        Triple(RReal(10.0), RReal(0.0), falseLogical),
        Triple(RReal(10.0), RReal(10.0), falseLogical)
    )

    @Test
    fun lessThanWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RReal(10.0) lessThan it }
        assertThrows<RapiraInvalidOperationError> { RReal(0.0) lessThan it }
        assertThrows<RapiraInvalidOperationError> { RReal(-10.0) lessThan it }
    }

    @TestFactory
    fun greaterThanWithNumberReturnsLogical() = makeObjectOperationTests(
        ">",
        { a: RObject, b: RObject -> a greaterThan b },

        // Integer
        Triple(RReal(0.0), RInteger(10), falseLogical),
        Triple(RReal(10.0), RInteger(0), trueLogical),
        Triple(RReal(10.0), RInteger(10), falseLogical),

        // Real
        Triple(RReal(0.0), RReal(10.0), falseLogical),
        Triple(RReal(10.0), RReal(0.0), trueLogical),
        Triple(RReal(10.0), RReal(10.0), falseLogical)
    )

    @Test
    fun greaterThanWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RReal(10.0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RReal(0.0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RReal(-10.0) greaterThan it }
    }

    @TestFactory
    fun lessThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        "<=",
        { a: RObject, b: RObject -> a lessThanEqualTo b },

        // Integer
        Triple(RReal(0.0), RInteger(10), trueLogical),
        Triple(RReal(10.0), RInteger(0), falseLogical),
        Triple(RReal(10.0), RInteger(10), trueLogical),

        // Real
        Triple(RReal(0.0), RReal(10.0), trueLogical),
        Triple(RReal(10.0), RReal(0.0), falseLogical),
        Triple(RReal(10.0), RReal(10.0), trueLogical)
    )

    @Test
    fun lessThanEqualToWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RReal(10.0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RReal(0.0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RReal(-10.0) lessThanEqualTo it }
    }

    @TestFactory
    fun greaterThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        ">=",
        { a: RObject, b: RObject -> a greaterThanEqualTo b },

        // Integer
        Triple(RReal(0.0), RInteger(10), falseLogical),
        Triple(RReal(10.0), RInteger(0), trueLogical),
        Triple(RReal(10.0), RInteger(10), trueLogical),

        // Real
        Triple(RReal(0.0), RReal(10.0), falseLogical),
        Triple(RReal(10.0), RReal(0.0), trueLogical),
        Triple(RReal(10.0), RReal(10.0), trueLogical)
    )

    @Test
    fun greaterThanEqualToWithOtherTypesThrowsException() = listOf(
        REmpty,
        RProcedure(),
        RFunction(),
        RText("hello"),
        RLogical(true),
        RSequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RReal(10.0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RReal(0.0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RReal(-10.0) greaterThanEqualTo it }
    }

    @Test
    fun toStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("0.0", RReal(0.0).toString())
        Assertions.assertEquals("1.23456789", RReal(1.23456789).toString())
        Assertions.assertEquals("-1.23456789", RReal(-1.23456789).toString())
    }
}
