package com.mattmik.rapira.objects

import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.toSuccess
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

/**
 * An integer number Rapira object value. For example, `125`.
 */
data class RInteger(val value: Int) : RObject {

    override val typeName: String
        get() = "integer"

    override fun plus(other: RObject) = when (other) {
        is RInteger -> RInteger(value + other.value).toSuccess()
        is Real -> Real(value + other.value).toSuccess()
        else -> super.plus(other)
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
        is Text -> when {
            value >= 0 -> Text(other.value.repeat(value)).toSuccess()
            else -> Result.Error("Cannot multiply text by negative integer")
        }
        is Sequence -> when {
            value >= 0 -> Sequence(arrayOfNulls<RObject>(value).flatMap { other.entries }).toSuccess()
            else -> Result.Error("Cannot multiply sequence by negative integer")
        }
        else -> super.times(other)
    }

    override fun div(other: RObject) = when (other) {
        is RInteger -> when {
            other.value == 0 -> Result.Error("Invalid division operation with zero value")
            value % other.value == 0 -> RInteger(value / other.value).toSuccess()
            else -> Real(value.toDouble() / other.value).toSuccess()
        }
        is Real -> when (other.value) {
            0.0, -0.0 -> Result.Error("Invalid division operation with zero value")
            else -> Real(value / other.value).toSuccess()
        }
        else -> super.div(other)
    }

    override fun intDivide(other: RObject) = when (other) {
        is RInteger -> when {
            other.value == 0 -> Result.Error("Invalid integer division operation with zero value")
            other.value < 0 -> Result.Error("Invalid integer division operation with negative value")
            else -> RInteger(value / other.value).toSuccess()
        }
        else -> super.intDivide(other)
    }

    override fun rem(other: RObject) = when (other) {
        is RInteger -> when {
            other.value == 0 -> Result.Error("Invalid remainder operation with zero value")
            other.value < 0 -> Result.Error("Invalid remainder operation with negative value")
            else -> RInteger(value % other.value).toSuccess()
        }
        else -> super.rem(other)
    }

    override fun power(other: RObject) = when (other) {
        is RInteger -> RInteger(value.toDouble().pow(other.value).toInt()).toSuccess()
        is Real -> Real(exp(ln(value.toDouble()) * other.value)).toSuccess()
        else -> super.power(other)
    }

    override fun compare(other: RObject) = when (other) {
        is RInteger -> value.compareTo(other.value).toSuccess()
        is Real -> value.compareTo(other.value).toSuccess()
        else -> super.compare(other)
    }

    override fun toString() = "$value"
}
