package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class AdditionTest {

    private val addOperation = { a: RapiraObject, b: RapiraObject -> a.add(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(10)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(10))
    ).map { (first, second, expected) ->
        dynamicTest("$first + $second = $expected") {
            assertEquals(
                expected,
                addOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first + $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                addOperation(
                    first,
                    second
                )
            }
        }
    }
}

class SubtractionTest {

    private val subtractOperation = { a: RapiraObject, b: RapiraObject -> a.subtract(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(4)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(-4))
    ).map { (first, second, expected) ->
        dynamicTest("$first - $second = $expected") {
            assertEquals(
                expected,
                subtractOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first - $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                subtractOperation(
                    first,
                    second
                )
            }
        }
    }
}

class NegationTest {

    private val negateOperation = { a: RapiraObject -> a.negate() };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Pair(RapiraInteger(7), RapiraInteger(-7)),
        Pair(RapiraInteger(-7), RapiraInteger(7))
    ).map { (value, expected) ->
        dynamicTest("-($value) = $expected") {
            assertEquals(
                expected,
                negateOperation(value)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        RapiraEmpty
    ).map { value ->
        dynamicTest("-($value) throws error") {
            assertThrows<RapiraInvalidOperationError> { negateOperation(value) }
        }
    }
}

class RapiraEmptyTest {

    private val otherObject = RapiraInteger(4)

    @Test
    fun multiplyThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.multiply(otherObject) }
    }

    @Test
    fun divideThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.divide(otherObject) }
    }

    @Test
    fun intDivideThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.intDivide(otherObject) }
    }

    @Test
    fun modulusThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.modulus(otherObject) }
    }

    @Test
    fun powerThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.power(otherObject) }
    }
}

class RapiraIntegerTest {

    private val firstInteger = RapiraInteger(7)
    private val secondInteger = RapiraInteger(3)

    @Test
    fun multiplyReturnsInteger() = assertEquals(RapiraInteger(21), firstInteger.multiply(secondInteger))

    @Test
    fun intDivideReturnsInteger() = assertEquals(RapiraInteger(2), firstInteger.intDivide(secondInteger))

    @Test
    fun modulusReturnsInteger() = assertEquals(RapiraInteger(1), firstInteger.modulus(secondInteger))

    @Test
    fun powerReturnsInteger() = assertEquals(RapiraInteger(343), firstInteger.power(secondInteger))
}