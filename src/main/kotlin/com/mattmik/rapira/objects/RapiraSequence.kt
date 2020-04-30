package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class RapiraSequence(val entries: List<RapiraObject> = emptyList()) : RapiraObject("sequence") {
    override fun plus(other: RapiraObject) = when (other) {
        is RapiraSequence -> RapiraSequence(entries + other.entries)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun times(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraSequence(arrayOfNulls<RapiraObject>(other.value).flatMap { entries })
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun length() = RapiraInteger(entries.size)

    override fun elementAt(other: RapiraObject) = when (other) {
        is RapiraInteger -> {
            val index = other.value
            if (index < 1 || index > entries.size) {
                throw RapiraIndexOutOfBoundsError(index)
            }
            entries[index - 1]
        }
        else -> throw RapiraInvalidOperationError(Operation.ElementAt, other)
    }

    override fun slice(start: RapiraObject?, end: RapiraObject?): RapiraObject {
        val startIndex = start ?: 1.toRapiraInteger()
        val endIndex = end ?: length()
        if (startIndex !is RapiraInteger || endIndex !is RapiraInteger) {
            throw RapiraIllegalArgumentException("Cannot invoke slice with non-integer arguments")
        }
        return entries.subList(startIndex.value - 1, endIndex.value).toRapiraSequence()
    }

    override fun toString() = if (entries.isEmpty()) "<* *>" else entries.joinToString(prefix = "<* ", postfix = " *>")
}
