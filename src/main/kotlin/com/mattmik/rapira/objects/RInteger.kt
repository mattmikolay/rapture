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
        is RInteger -> RInteger(value - other.value).toSuccess()
        is Real -> Real(value - other.value).toSuccess()
        else -> super.minus(other)
    }

    override fun negate() =
        RInteger(-value).toSuccess()

    override fun times(other: RObject) = when (other) {
        is RInteger -> RInteger(value * other.value).toSuccess()
        is Real -> Real(value * other.value).toSuccess()
        is Text -> Text(other.value.repeat(value)).toSuccess()
        is Sequence -> Sequence(arrayOfNulls<RObject>(value).flatMap { other.entries }).toSuccess()
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun div(other: RObject) = when (other) {
        is RInteger -> if (value % other.value == 0)
            RInteger(value / other.value).toSuccess()
        else Real(value.toDouble() / other.value).toSuccess()
        is Real -> Real(value / other.value).toSuccess()
        else -> super.div(other)
    }

    override fun intDivide(other: RObject) = when (other) {
        is RInteger -> {
            if (other.value <= 0)
                OperationResult.Error("Cannot perform integer division with negative value")
            else
                RInteger(value / other.value).toSuccess()
        }
        else -> super.intDivide(other)
    }

    override fun rem(other: RObject) = when (other) {
        is RInteger -> RInteger(value % other.value).toSuccess()
        else -> super.rem(other)
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
