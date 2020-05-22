package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln

data class Real(val value: Double) : RObject {
    override fun plus(other: RObject) = when (other) {
        is RInteger -> Real(value + other.value).toSuccess()
        is Real -> Real(value + other.value).toSuccess()
        else -> super.plus(other)
    }

    override fun minus(other: RObject) = when (other) {
        is RInteger -> Real(value - other.value).toSuccess()
        is Real -> Real(value - other.value).toSuccess()
        else -> super.minus(other)
    }

    override fun negate() =
        Real(-value).toSuccess()

    override fun times(other: RObject) = when (other) {
        is RInteger -> Real(value * other.value).toSuccess()
        is Real -> Real(value * other.value).toSuccess()
        else -> super.times(other)
    }

    override fun div(other: RObject) = when (other) {
        is RInteger -> Real(value / other.value).toSuccess()
        is Real -> Real(value / other.value).toSuccess()
        else -> super.div(other)
    }

    override fun power(other: RObject) = when (other) {
        is RInteger -> Real(exp(ln(value) * other.value)).toSuccess()
        is Real -> Real(exp(ln(value) * other.value)).toSuccess()
        else -> super.power(other)
    }

    override fun compareTo(other: RObject) = when (other) {
        is RInteger -> value.compareTo(other.value)
        is Real -> value.compareTo(other.value)
        else -> throw RapiraInvalidOperationError("Cannot compare")
    }

    override fun toString() = "$value"
}
