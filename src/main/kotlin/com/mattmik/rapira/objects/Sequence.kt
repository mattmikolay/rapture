package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class Sequence(val entries: List<RObject> = emptyList()) : RObject("sequence") {
    constructor(vararg entries: RObject) : this(entries.toList())

    override fun plus(other: RObject) = when (other) {
        is Sequence -> Sequence(entries + other.entries)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun times(other: RObject) = when (other) {
        is RInteger -> {
            if (other.value >= 0)
                Sequence(
                    List(entries.size * other.value) { index -> entries[index % entries.size] }
                ).toSuccess()
            else
                OperationResult.Error("Cannot multiply sequence by negative number")
        }
        else -> super.times(other)
    }

    override fun length() =
        RInteger(entries.size).toSuccess()

    override fun elementAt(other: RObject) = when (other) {
        is RInteger -> {
            val index = other.value
            if (index < 1 || index > entries.size) {
                OperationResult.Error("Index $index is out of bounds")
            } else {
                entries[index - 1].toSuccess()
            }
        }
        else -> super.elementAt(other)
    }

    override fun slice(start: RObject?, end: RObject?): RObject {
        val startIndex = start ?: 1.toRInteger()
        val endIndex = end ?: entries.size.toRInteger()
        if (startIndex !is RInteger || endIndex !is RInteger) {
            throw RapiraIllegalArgumentException("Cannot invoke slice with non-integer arguments")
        }
        return entries.subList(startIndex.value - 1, endIndex.value).toSequence()
    }

    override fun toString() = if (entries.isEmpty()) "<* *>" else entries.joinToString(prefix = "<* ", postfix = " *>")
}
