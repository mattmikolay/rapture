package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RapiraEmptyTest {

    private val otherObject = RapiraInteger(4)

    @Test
    fun addThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.add(otherObject) }
    }

    @Test
    fun subtractThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.subtract(otherObject) }
    }

    @Test
    fun negateThrowsInvalidOperationError() {
        assertThrows<RapiraInvalidOperationError> { RapiraEmpty.negate() }
    }

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
    fun addReturnsInteger() = assertEquals(RapiraInteger(10), firstInteger.add(secondInteger))

    @Test
    fun subtractReturnsInteger() = assertEquals(RapiraInteger(4), firstInteger.subtract(secondInteger))

    @Test
    fun negateReturnsInteger() = assertEquals(RapiraInteger(-7), firstInteger.negate())

    @Test
    fun multiplyReturnsInteger() = assertEquals(RapiraInteger(21), firstInteger.multiply(secondInteger))

    @Test
    fun intDivideReturnsInteger() = assertEquals(RapiraInteger(2), firstInteger.intDivide(secondInteger))

    @Test
    fun modulusReturnsInteger() = assertEquals(RapiraInteger(1), firstInteger.modulus(secondInteger))

    @Test
    fun powerReturnsInteger() = assertEquals(RapiraInteger(343), firstInteger.power(secondInteger))
}