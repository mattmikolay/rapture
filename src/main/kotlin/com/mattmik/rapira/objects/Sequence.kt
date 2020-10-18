package com.mattmik.rapira.objects

import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.toSuccess

/**
 * A sequence Rapira object value. For example, `<* 1, 2.5, yes, "Hello" *>`.
 */
data class Sequence(val entries: List<RObject> = emptyList()) : RObject {
    constructor(vararg entries: RObject) : this(entries.toList())

    override fun plus(other: RObject) = when (other) {
        is Sequence -> Sequence(entries + other.entries).toSuccess()
        else -> super.plus(other)
    }

    override fun times(other: RObject) = when (other) {
        is RInteger -> {
            if (other.value >= 0)
                Sequence(
                    List(entries.size * other.value) { index -> entries[index % entries.size] }
                ).toSuccess()
            else
                Result.Error("Cannot multiply sequence by negative number")
        }
        else -> super.times(other)
    }

    override fun length() =
        RInteger(entries.size).toSuccess()

    override fun elementAt(other: RObject) = when (other) {
        is RInteger -> {
            val index = other.value
            if (index < 1 || index > entries.size) {
                Result.Error("Index $index is out of bounds")
            } else {
                entries[index - 1].toSuccess()
            }
        }
        else -> Result.Error("Cannot index with non-integer value")
    }

    override fun slice(start: RObject?, end: RObject?): Result<RObject> {
        val startIndex = start ?: 1.toRInteger()
        val endIndex = end ?: entries.size.toRInteger()
        if (startIndex !is RInteger || endIndex !is RInteger) {
            return Result.Error("Cannot invoke slice with non-integer arguments")
        }
        if (endIndex.value - startIndex.value + 1 < 0) {
            return Result.Error("Indexes $startIndex and $endIndex are out of bounds")
        }
        if (startIndex.value > endIndex.value) {
            return emptyList<RObject>().toSequence().toSuccess()
        }
        if (startIndex.value < 1) {
            return Result.Error("Start index $startIndex is out of bounds")
        }
        if (endIndex.value > entries.size) {
            return Result.Error("End index $endIndex is out of bounds")
        }
        return entries.subList(startIndex.value - 1, endIndex.value).toSequence().toSuccess()
    }

    override fun toString() = if (entries.isEmpty()) "<* *>" else entries.joinToString(prefix = "<* ", postfix = " *>")
}
