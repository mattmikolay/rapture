package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

data class RInteger(val value: Int) : RObject("integer") {
    override fun plus(other: RObject) = when (other) {
        is RInteger -> RInteger(value + other.value)
        is Real -> Real(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun minus(other: RObject) = when (other) {
        is RInteger -> RInteger(value - other.value)
        is Real -> Real(value - other.value)
        else -> throw RapiraInvalidOperationError(Operation.Subtraction, other)
    }

    override fun negate() =
        RInteger(-value).toSuccess()

    override fun times(other: RObject) = when (other) {
        is RInteger -> RInteger(value * other.value)
        is Real -> Real(value * other.value)
        is Text -> Text(other.value.repeat(value))
        is Sequence -> Sequence(arrayOfNulls<RObject>(value).flatMap { other.entries })
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun div(other: RObject) = when (other) {
        is RInteger -> if (value % other.value == 0)
            RInteger(value / other.value)
        else Real(value.toDouble() / other.value)
        is Real -> Real(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.Division, other)
    }

    override fun intDivide(other: RObject) = when (other) {
        is RInteger -> {
            if (other.value <= 0)
                throw RapiraInvalidOperationError("cannot perform ${Operation.IntDivision} with values less than or equal to 0")
            else RInteger(value / other.value)
        }
        else -> throw RapiraInvalidOperationError(Operation.IntDivision, other)
    }

    override fun rem(other: RObject) = when (other) {
        is RInteger -> RInteger(value % other.value)
        else -> throw RapiraInvalidOperationError(Operation.Modulo, other)
    }

    override fun power(other: RObject) = when (other) {
        is RInteger -> RInteger(value.toDouble().pow(other.value).toInt()).toSuccess()
        is Real -> Real(exp(ln(value.toDouble()) * other.value)).toSuccess()
        else -> super.power(other)
    }

    override fun compareTo(other: RObject) = when (other) {
        is RInteger -> value.compareTo(other.value)
        is Real -> value.compareTo(other.value)
        else -> throw RapiraInvalidOperationError("Cannot compare")
    }

    override fun toString() = "$value"
}
