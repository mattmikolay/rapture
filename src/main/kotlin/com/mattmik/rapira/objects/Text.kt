package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class Text(val value: String) : RObject("text") {

    override fun plus(other: RObject) = when (other) {
        is Text -> Text(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun times(other: RObject) = when (other) {
        is RInteger -> {
            if (other.value >= 0)
                value.repeat(other.value).toText().toSuccess()
            else
                OperationResult.Error("Cannot multiply text by negative number")
        }
        else -> super.times(other)
    }

    override fun length() =
        RInteger(value.length).toSuccess()

    override fun elementAt(other: RObject) = when (other) {
        is RInteger -> {
            val index = other.value
            if (index < 1 || index > value.length) {
                throw RapiraIndexOutOfBoundsError(index)
            }
            value[index - 1].toString().toText()
        }
        else -> throw RapiraInvalidOperationError(Operation.ElementAt, other)
    }

    override fun slice(start: RObject?, end: RObject?): RObject {
        val startIndex = start ?: 1.toRInteger()
        val endIndex = end ?: value.length.toRInteger()
        if (startIndex !is RInteger || endIndex !is RInteger) {
            throw RapiraIllegalArgumentException("Cannot invoke slice with non-integer arguments")
        }
        return value.substring(startIndex.value - 1, endIndex.value).toText()
    }

    override fun toString() =
        "\"${value.replace("\"\"", "\"")}\""
}
