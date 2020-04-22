package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class RapiraText(val value: String) : RapiraObject("text") {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraText -> RapiraText(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraText(value.repeat(other.value))
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun length() = RapiraInteger(value.length)

    override fun toString() = "\"${value.replace("\"\"", "\"")}\""
}
