package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

sealed class RapiraObject(val typeDescription: String) {
    open fun add(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Addition, this)

    open fun subtract(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Subtraction, this)

    open fun negate(): RapiraObject = throw RapiraInvalidOperationError(Operation.Negation, this)

    open fun multiply(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Multiplication, this)

    open fun divide(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Division, this)

    open fun intDivide(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.IntDivision, this)

    open fun modulus(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Modulo, this)

    open fun power(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Exponentiation, this)
}

object RapiraEmpty : RapiraObject("empty") {
    override fun toString() = "empty"
}

data class RapiraLogical(val value: Boolean) : RapiraObject("logical") {
    // TODO Implement operations

    override fun toString() = if (value) "yes" else "no"
}

object RapiraProcedure : RapiraObject("procedure") {
    override fun toString() = "procedure"
}

object RapiraFunction : RapiraObject("function") {
    override fun toString() = "function"
}

data class RapiraInteger(val value: Int) : RapiraObject("integer") {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value + other.value)
        is RapiraReal -> RapiraReal(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun subtract(other: RapiraObject) = when (other) {
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

    override fun toString() = "$value"
}

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

data class RapiraText(val value: String) : RapiraObject("text") {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraText -> RapiraText(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraText(value.repeat(other.value))
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun toString() = "\"${value.replace("\"\"", "\"")}\""
}

data class RapiraSequence(val entries: List<RapiraObject> = emptyList()) : RapiraObject("sequence") {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraSequence -> RapiraSequence(entries + other.entries)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraSequence(arrayOfNulls<RapiraObject>(other.value).flatMap { entries })
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun toString() = if (entries.isEmpty()) "<* *>" else entries.joinToString(prefix = "<* ", postfix = " *>")
}
