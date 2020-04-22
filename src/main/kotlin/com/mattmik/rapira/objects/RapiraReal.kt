package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln

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

    override fun power(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraReal(exp(ln(value) * other.value))
        is RapiraReal -> RapiraReal(exp(ln(value) * other.value))
        else -> throw RapiraInvalidOperationError(Operation.Exponentiation, other)
    }

    override fun toString() = "$value"
}
