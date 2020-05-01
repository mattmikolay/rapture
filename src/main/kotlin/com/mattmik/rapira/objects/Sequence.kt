package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class Sequence(val entries: List<RObject> = emptyList()) : RObject("sequence") {
    override fun plus(other: RObject) = when (other) {
        is Sequence -> Sequence(entries + other.entries)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun times(other: RObject) = when (other) {
        is RInteger -> Sequence(arrayOfNulls<RObject>(other.value).flatMap { entries })
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun length() = RInteger(entries.size)

    override fun elementAt(other: RObject) = when (other) {
        is RInteger -> {
            val index = other.value
            if (index < 1 || index > entries.size) {
                throw RapiraIndexOutOfBoundsError(index)
            }
            entries[index - 1]
        }
        else -> throw RapiraInvalidOperationError(Operation.ElementAt, other)
    }

    override fun slice(start: RObject?, end: RObject?): RObject {
        val startIndex = start ?: 1.toRInteger()
        val endIndex = end ?: length()
        if (startIndex !is RInteger || endIndex !is RInteger) {
            throw RapiraIllegalArgumentException("Cannot invoke slice with non-integer arguments")
        }
        return entries.subList(startIndex.value - 1, endIndex.value).toSequence()
    }

    override fun toString() = if (entries.isEmpty()) "<* *>" else entries.joinToString(prefix = "<* ", postfix = " *>")
}
