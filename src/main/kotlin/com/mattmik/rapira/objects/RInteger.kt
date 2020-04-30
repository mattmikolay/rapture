package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

data class RInteger(val value: Int) : RObject("integer") {
    override fun plus(other: RObject) = when (other) {
        is RInteger -> RInteger(value + other.value)
        is RReal -> RReal(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun minus(other: RObject) = when (other) {
        is RInteger -> RInteger(value - other.value)
        is RReal -> RReal(value - other.value)
        else -> throw RapiraInvalidOperationError(Operation.Subtraction, other)
    }

    override fun negate(): RObject = RInteger(-value)

    override fun times(other: RObject) = when (other) {
        is RInteger -> RInteger(value * other.value)
        is RReal -> RReal(value * other.value)
        is RText -> RText(other.value.repeat(value))
        is RSequence -> RSequence(arrayOfNulls<RObject>(value).flatMap { other.entries })
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun div(other: RObject) = when (other) {
        is RInteger -> if (value % other.value == 0)
            RInteger(value / other.value)
        else RReal(value.toDouble() / other.value)
        is RReal -> RReal(value / other.value)
        else -> throw RapiraInvalidOperationError(Operation.Division, other)
    }

    // TODO Look into additional quirks of Rapira's integer division operation
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

    // TODO Look into additional quirks of Rapira's exponentiation operation
    override fun power(other: RObject) = when (other) {
        is RInteger -> RInteger(value.toDouble().pow(other.value).toInt())
        is RReal -> RReal(exp(ln(value.toDouble()) * other.value))
        else -> throw RapiraInvalidOperationError(Operation.Exponentiation, other)
    }

    override fun lessThan(other: RObject) = when (other) {
        is RInteger -> RLogical(value < other.value)
        is RReal -> RLogical(value < other.value)
        else -> throw RapiraInvalidOperationError(Operation.LessThan, other)
    }

    override fun greaterThan(other: RObject) = when (other) {
        is RInteger -> RLogical(value > other.value)
        is RReal -> RLogical(value > other.value)
        else -> throw RapiraInvalidOperationError(Operation.GreaterThan, other)
    }

    override fun lessThanEqualTo(other: RObject) = when (other) {
        is RInteger -> RLogical(value <= other.value)
        is RReal -> RLogical(value <= other.value)
        else -> throw RapiraInvalidOperationError(Operation.LessThanEqualTo, other)
    }

    override fun greaterThanEqualTo(other: RObject) = when (other) {
        is RInteger -> RLogical(value >= other.value)
        is RReal -> RLogical(value >= other.value)
        else -> throw RapiraInvalidOperationError(Operation.GreaterThanEqualTo, other)
    }

    override fun toString() = "$value"
}
