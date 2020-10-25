package com.mattmik.rapira.objects

import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.toSuccess
import kotlin.math.exp
import kotlin.math.ln

/**
 * A real number Rapira object value. For example, `530.84` or `1.9e-8`.
 */
data class Real(val value: Double) : RObject {

    override val typeName: String
        get() = "real"

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
        is RInteger -> {
            if (other.value != 0)
                Real(value / other.value).toSuccess()
            else
                Result.Error("Invalid division operation with zero value")
        }
        is Real -> when (other.value) {
            0.0, -0.0 -> Result.Error("Invalid division operation with zero value")
            else -> Real(value / other.value).toSuccess()
        }
        else -> super.div(other)
    }

    override fun power(other: RObject) = when (other) {
        is RInteger -> Real(exp(ln(value) * other.value)).toSuccess()
        is Real -> Real(exp(ln(value) * other.value)).toSuccess()
        else -> super.power(other)
    }

    override fun compare(other: RObject) = when (other) {
        is RInteger -> value.compareTo(other.value).toSuccess()
        is Real -> value.compareTo(other.value).toSuccess()
        else -> super.compare(other)
    }

    override fun toString() = "$value"
}
