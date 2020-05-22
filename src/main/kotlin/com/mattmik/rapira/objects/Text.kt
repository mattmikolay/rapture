package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class Text(val value: String) : RObject {

    override fun plus(other: RObject) = when (other) {
        is Text -> Text(value + other.value)
        else -> throw RapiraInvalidOperationError(Operation.Addition)
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
                OperationResult.Error("Index $index is out of bounds")
            } else {
                value[index - 1].toString().toText().toSuccess()
            }
        }
        else -> super.elementAt(other)
    }

    override fun slice(start: RObject?, end: RObject?): OperationResult {
        val startIndex = start ?: 1.toRInteger()
        val endIndex = end ?: value.length.toRInteger()
        if (startIndex !is RInteger || endIndex !is RInteger) {
            return OperationResult.Error("Cannot invoke slice with non-integer arguments")
        }
        return value.substring(startIndex.value - 1, endIndex.value).toText().toSuccess()
    }

    override fun toString() =
        "\"${value.replace("\"\"", "\"")}\""
}
