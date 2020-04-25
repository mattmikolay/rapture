package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class RapiraText(val value: String) : RapiraObject("text") {

    override fun add(other: RapiraObject) = when (other) {
        is RapiraText -> RapiraText(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger ->
            if (other.value >= 0)
                value.repeat(other.value).toRapiraText()
            else
                throw RapiraInvalidOperationError("Cannot multiply text by negative number")
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun length() = RapiraInteger(value.length)

    override fun elementAt(other: RapiraObject) = when (other) {
        is RapiraInteger -> {
            val index = other.value
            if (index < 1 || index > value.length) {
                throw RapiraIndexOutOfBoundsError(index)
            }
            value[index - 1].toString().toRapiraText()
        }
        else -> throw RapiraInvalidOperationError(Operation.ElementAt, other)
    }

    override fun toString() = "\"${value.replace("\"\"", "\"")}\""
}
