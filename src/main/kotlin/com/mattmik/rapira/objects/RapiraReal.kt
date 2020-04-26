package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln

data class RapiraReal(val value: Double) : RapiraObject("real number") {
    override fun plus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value + other.value)
        is RapiraReal -> RapiraReal(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun minus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value - other.value)
        is RapiraReal -> RapiraReal(value - other.value)
        else -> throw RapiraInvalidOperationError(Operation.Subtraction, other)
    }

    override fun negate(): RapiraObject = RapiraReal(-value)

    override fun times(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value * other.value)
        is RapiraReal -> RapiraReal(value * other.value)
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun div(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(value / other.value)
        is RapiraReal -> RapiraReal(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.Division, other)
    }

    override fun power(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(exp(ln(value) * other.value))
        is RapiraReal -> RapiraReal(exp(ln(value) * other.value))
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
