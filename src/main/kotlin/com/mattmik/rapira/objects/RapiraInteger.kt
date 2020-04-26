package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

data class RapiraInteger(val value: Int) : RapiraObject("integer") {
    override fun plus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value + other.value)
        is RapiraReal -> RapiraReal(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun minus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value - other.value)
        is RapiraReal -> RapiraReal(value - other.value)
        else -> throw RapiraInvalidOperationError(Operation.Subtraction, other)
    }

    override fun negate(): RapiraObject = RapiraInteger(-value)

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value * other.value)
        is RapiraReal -> RapiraReal(value * other.value)
        is RapiraText -> RapiraText(other.value.repeat(value))
        is RapiraSequence -> RapiraSequence(arrayOfNulls<RapiraObject>(value).flatMap { other.entries })
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun divide(other: RapiraObject) = when (other) {
        is RapiraInteger -> if (value % other.value == 0)
            RapiraInteger(value / other.value)
        else RapiraReal(value.toDouble() / other.value)
        is RapiraReal -> RapiraReal(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.Division, other)
    }

    // TODO Look into additional quirks of Rapira's integer division operation
    override fun intDivide(other: RapiraObject) = when (other) {
        is RapiraInteger -> {
            if (other.value <= 0)
                throw RapiraInvalidOperationError("cannot perform ${Operation.IntDivision} with values less than or equal to 0")
            else RapiraInteger(value / other.value)
        }
        else -> throw RapiraInvalidOperationError(Operation.IntDivision, other)
    }

    override fun modulus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value % other.value)
        else -> throw RapiraInvalidOperationError(Operation.Modulo, other)
    }

    // TODO Look into additional quirks of Rapira's exponentiation operation
    override fun power(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value.toDouble().pow(other.value).toInt())
        is RapiraReal -> RapiraReal(exp(ln(value.toDouble()) * other.value))
        else -> throw RapiraInvalidOperationError(Operation.Exponentiation, other)
    }

    override fun lessThan(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraLogical(value < other.value)
        is RapiraReal -> RapiraLogical(value < other.value)
        else -> throw RapiraInvalidOperationError(Operation.LessThan, other)
    }

    override fun greaterThan(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraLogical(value > other.value)
        is RapiraReal -> RapiraLogical(value > other.value)
        else -> throw RapiraInvalidOperationError(Operation.GreaterThan, other)
    }

    override fun lessThanEqualTo(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraLogical(value <= other.value)
        is RapiraReal -> RapiraLogical(value <= other.value)
        else -> throw RapiraInvalidOperationError(Operation.LessThanEqualTo, other)
    }

    override fun greaterThanEqualTo(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraLogical(value >= other.value)
        is RapiraReal -> RapiraLogical(value >= other.value)
        else -> throw RapiraInvalidOperationError(Operation.GreaterThanEqualTo, other)
    }

    override fun toString() = "$value"
}
