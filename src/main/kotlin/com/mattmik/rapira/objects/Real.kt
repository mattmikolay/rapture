package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln

data class Real(val value: Double) : RObject("real number") {
    override fun plus(other: RObject) = when (other) {
        is RInteger -> Real(value + other.value)
        is Real -> Real(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun minus(other: RObject) = when (other) {
        is RInteger -> Real(value - other.value)
        is Real -> Real(value - other.value)
        else -> throw RapiraInvalidOperationError(Operation.Subtraction, other)
    }

    override fun negate(): RObject = Real(-value)

    override fun times(other: RObject) = when (other) {
        is RInteger -> Real(value * other.value)
        is Real -> Real(value * other.value)
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun div(other: RObject) = when (other) {
        is RInteger -> Real(value / other.value)
        is Real -> Real(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.Division, other)
    }

    override fun power(other: RObject) = when (other) {
        is RInteger -> Real(exp(ln(value) * other.value))
        is Real -> Real(exp(ln(value) * other.value))
        else -> throw RapiraInvalidOperationError(Operation.Exponentiation, other)
    }

    override fun lessThan(other: RObject) = when (other) {
        is RInteger -> Logical(value < other.value)
        is Real -> Logical(value < other.value)
        else -> throw RapiraInvalidOperationError(Operation.LessThan, other)
    }

    override fun greaterThan(other: RObject) = when (other) {
        is RInteger -> Logical(value > other.value)
        is Real -> Logical(value > other.value)
        else -> throw RapiraInvalidOperationError(Operation.GreaterThan, other)
    }

    override fun lessThanEqualTo(other: RObject) = when (other) {
        is RInteger -> Logical(value <= other.value)
        is Real -> Logical(value <= other.value)
        else -> throw RapiraInvalidOperationError(Operation.LessThanEqualTo, other)
    }

    override fun greaterThanEqualTo(other: RObject) = when (other) {
        is RInteger -> Logical(value >= other.value)
        is Real -> Logical(value >= other.value)
        else -> throw RapiraInvalidOperationError(Operation.GreaterThanEqualTo, other)
    }

    override fun toString() = "$value"
}
