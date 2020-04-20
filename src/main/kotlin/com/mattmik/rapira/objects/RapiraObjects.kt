package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.pow

sealed class RapiraObject {
    abstract fun add(other: RapiraObject): RapiraObject
    abstract fun subtract(other: RapiraObject): RapiraObject
    abstract fun negate(): RapiraObject
    abstract fun multiply(other: RapiraObject): RapiraObject
    abstract fun divide(other: RapiraObject): RapiraObject
    abstract fun intDivide(other: RapiraObject): RapiraObject
    abstract fun modulus(other: RapiraObject): RapiraObject
    abstract fun power(other: RapiraObject): RapiraObject
}

object RapiraEmpty : RapiraObject() {
    override fun add(other: RapiraObject)
            = throw RapiraInvalidOperationError("cannot perform addition using an empty value")

    override fun subtract(other: RapiraObject)
            = throw RapiraInvalidOperationError("cannot perform subtraction using an empty value")

    override fun negate(): RapiraObject
            = throw RapiraInvalidOperationError("cannot negate an empty value")

    override fun multiply(other: RapiraObject)
            = throw RapiraInvalidOperationError("cannot perform multiplication using an empty value")

    override fun divide(other: RapiraObject)
            = throw RapiraInvalidOperationError("cannot perform division using an empty value")

    override fun intDivide(other: RapiraObject)
            = throw RapiraInvalidOperationError("cannot perform integer division using an empty value")

    override fun modulus(other: RapiraObject)
            = throw RapiraInvalidOperationError("cannot perform modulo operation using an empty value")

    override fun power(other: RapiraObject)
            = throw RapiraInvalidOperationError("cannot perform exponentiation operation using an empty value")

    override fun toString() = "empty"
}

data class RapiraInteger(val value: Int) : RapiraObject() {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value + other.value)
        is RapiraReal -> RapiraReal(value + other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform addition using an empty value")
    }

    override fun subtract(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value - other.value)
        is RapiraReal -> RapiraReal(value - other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform subtraction using an empty value")
    }

    override fun negate(): RapiraObject = RapiraInteger(-value)

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value * other.value)
        is RapiraReal -> RapiraReal(value * other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform multiplication using an empty value")
    }

    override fun divide(other: RapiraObject) = when (other) {
        is RapiraInteger -> TODO("Not yet implemented")
        is RapiraReal -> RapiraReal(value / other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform division using an empty value")
    }

    override fun intDivide(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value / other.value)
        is RapiraReal -> TODO("Not yet implemented")
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform integer division using an empty value")
    }

    override fun modulus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value % other.value)
        is RapiraReal -> TODO("Not yet implemented")
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform modulo operation using an empty value")
    }

    override fun power(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value.toDouble().pow(other.value).toInt())
        is RapiraReal -> TODO("Not yet implemented")
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform exponentiation operation using an empty value")
    }

    override fun toString() = "$value"
}

data class RapiraReal(val value: Double) : RapiraObject() {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value + other.value)
        is RapiraReal -> RapiraReal(value + other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform addition using an empty value")
    }

    override fun subtract(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value - other.value)
        is RapiraReal -> RapiraReal(value - other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform subtraction using an empty value")
    }

    override fun negate(): RapiraObject = RapiraReal(-value)

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value * other.value)
        is RapiraReal -> RapiraReal(value * other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform multiplication using an empty value")
    }

    override fun divide(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value / other.value)
        is RapiraReal -> RapiraReal(value / other.value)
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform division using an empty value")
    }

    override fun intDivide(other: RapiraObject) = when (other) {
        is RapiraInteger -> TODO("Not yet implemented")
        is RapiraReal -> TODO("Not yet implemented")
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform integer division using an empty value")
    }

    override fun modulus(other: RapiraObject) = when (other) {
        is RapiraInteger -> TODO("Not yet implemented")
        is RapiraReal -> TODO("Not yet implemented")
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform modulo operation using an empty value")
    }

    override fun power(other: RapiraObject) = when (other) {
        is RapiraInteger -> TODO("Not yet implemented")
        is RapiraReal -> TODO("Not yet implemented")
        is RapiraEmpty -> throw RapiraInvalidOperationError("cannot perform exponentiation operation using an empty value")
    }

    override fun toString() = "$value"
}
