package com.mattmik.rapira.objects

import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.toSuccess

/**
 * A text Rapira object value. For example, `"Hello, world!"`.
 */
data class Text(val value: String) : RObject {

    override val typeName: String
        get() = "text"

    override fun plus(other: RObject) = when (other) {
        is Text -> Text(value + other.value).toSuccess()
        else -> super.plus(other)
    }

    override fun times(other: RObject) = when (other) {
        is RInteger -> {
            if (other.value >= 0)
                value.repeat(other.value).toText().toSuccess()
            else
                Result.Error("Cannot multiply text by negative number")
        }
        else -> super.times(other)
    }

    override fun length() =
        RInteger(value.length).toSuccess()

    override fun elementAt(other: RObject) = when (other) {
        is RInteger -> {
            val index = other.value
            if (index < 1 || index > value.length) {
                Result.Error("Index $index is out of bounds")
            } else {
                value[index - 1].toString().toText().toSuccess()
            }
        }
        else -> super.elementAt(other)
    }

    override fun slice(start: RObject?, end: RObject?): Result<RObject> {
        val startIndex = start ?: 1.toRInteger()
        val endIndex = end ?: value.length.toRInteger()
        if (startIndex !is RInteger || endIndex !is RInteger) {
            return Result.Error("Cannot invoke slice with non-integer arguments")
        }
        if (endIndex.value - startIndex.value + 1 < 0) {
            return Result.Error("Indexes $startIndex and $endIndex are out of bounds")
        }
        if (startIndex.value > endIndex.value) {
            return "".toText().toSuccess()
        }
        if (startIndex.value < 1) {
            return Result.Error("Start index $startIndex is out of bounds")
        }
        if (endIndex.value > value.length) {
            return Result.Error("End index $endIndex is out of bounds")
        }
        return value.substring(startIndex.value - 1, endIndex.value).toText().toSuccess()
    }

    override fun toString() =
        "\"${value.replace("\"\"", "\"")}\""
}
