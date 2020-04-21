package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.pow

sealed class RapiraObject(val typeDescription: String) {
    open fun add(other: RapiraObject): RapiraObject
        = throw RapiraInvalidOperationError(Operation.Addition, this)

    open fun subtract(other: RapiraObject): RapiraObject
        = throw RapiraInvalidOperationError(Operation.Subtraction, this)

    open fun negate(): RapiraObject
        = throw RapiraInvalidOperationError(Operation.Negation, this)

    open fun multiply(other: RapiraObject): RapiraObject
        = throw RapiraInvalidOperationError(Operation.Multiplication, this)

    open fun divide(other: RapiraObject): RapiraObject
        = throw RapiraInvalidOperationError(Operation.Division, this)

    open fun intDivide(other: RapiraObject): RapiraObject
        = throw RapiraInvalidOperationError(Operation.IntDivision, this)

    open fun modulus(other: RapiraObject): RapiraObject
        = throw RapiraInvalidOperationError(Operation.Modulo, this)

    open fun power(other: RapiraObject): RapiraObject
        = throw RapiraInvalidOperationError(Operation.Exponentiation, this)
}

object RapiraEmpty : RapiraObject("empty") {
    override fun toString() = "empty"
}

data class RapiraLogical(val value: Boolean) : RapiraObject("logical") {
    // TODO Implement operations

    override fun toString() = if (value) "yes" else "no"
}

object RapiraProcedure : RapiraObject("procedure")

object RapiraFunction : RapiraObject("function")

data class RapiraInteger(val value: Int) : RapiraObject("integer") {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value + other.value)
        is RapiraReal -> RapiraReal(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun subtract(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value - other.value)
        is RapiraReal -> RapiraReal(value - other.value)
        else -> throw RapiraInvalidOperationError(Operation.Subtraction, other)
    }

    override fun negate(): RapiraObject = RapiraInteger(-value)

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value * other.value)
        is RapiraReal -> RapiraReal(value * other.value)
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun divide(other: RapiraObject) = when (other) {
        is RapiraReal -> RapiraReal(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.Division, other)
    }

    override fun intDivide(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.IntDivision, other)
    }

    override fun modulus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value % other.value)
        else -> throw RapiraInvalidOperationError(Operation.Modulo, other)
    }

    override fun power(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value.toDouble().pow(other.value).toInt())
        else -> throw RapiraInvalidOperationError(Operation.Exponentiation, other)
    }

    override fun toString() = "$value"
}

data class RapiraReal(val value: Double) : RapiraObject("real number") {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value + other.value)
        is RapiraReal -> RapiraReal(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun subtract(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value - other.value)
        is RapiraReal -> RapiraReal(value - other.value)
        else -> throw RapiraInvalidOperationError(Operation.Subtraction, other)
    }

    override fun negate(): RapiraObject = RapiraReal(-value)

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value * other.value)
        is RapiraReal -> RapiraReal(value * other.value)
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun divide(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value / other.value)
        is RapiraReal -> RapiraReal(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.Division, other)
    }

    override fun toString() = "$value"
}

data class RapiraText(val value: String) : RapiraObject("text") {
    override fun toString() = "$value"
}

data class RapiraSequence(val entries: List<RapiraObject> = emptyList()) : RapiraObject("sequence")
