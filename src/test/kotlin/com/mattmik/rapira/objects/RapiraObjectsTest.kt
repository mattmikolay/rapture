package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.Test
import org.junit.Assert.assertEquals

class RapiraEmptyTest {

    private val otherObject = RapiraInteger(4)

    @Test(expected = RapiraInvalidOperationError::class)
    fun addThrowsInvalidOperationError() {
        RapiraEmpty.add(otherObject)
    }

    @Test(expected = RapiraInvalidOperationError::class)
    fun subtractThrowsInvalidOperationError() {
        RapiraEmpty.subtract(otherObject)
    }

    @Test(expected = RapiraInvalidOperationError::class)
    fun negateThrowsInvalidOperationError() {
        RapiraEmpty.negate()
    }

    @Test(expected = RapiraInvalidOperationError::class)
    fun multiplyThrowsInvalidOperationError() {
        RapiraEmpty.multiply(otherObject)
    }

    @Test(expected = RapiraInvalidOperationError::class)
    fun divideThrowsInvalidOperationError() {
        RapiraEmpty.divide(otherObject)
    }

    @Test(expected = RapiraInvalidOperationError::class)
    fun intDivideThrowsInvalidOperationError() {
        RapiraEmpty.intDivide(otherObject)
    }

    @Test(expected = RapiraInvalidOperationError::class)
    fun modulusThrowsInvalidOperationError() {
        RapiraEmpty.modulus(otherObject)
    }

    @Test(expected = RapiraInvalidOperationError::class)
    fun powerThrowsInvalidOperationError() {
        RapiraEmpty.power(otherObject)
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