package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class RapiraSequence(val entries: List<RapiraObject> = emptyList()) : RapiraObject("sequence") {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraSequence -> RapiraSequence(entries + other.entries)
        else -> throw RapiraInvalidOperationError(Operation.Addition, other)
    }

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraSequence(arrayOfNulls<RapiraObject>(other.value).flatMap { entries })
        else -> throw RapiraInvalidOperationError(Operation.Multiplication, other)
    }

    override fun length() = RapiraInteger(entries.size)

    override fun toString() = if (entries.isEmpty()) "<* *>" else entries.joinToString(prefix = "<* ", postfix = " *>")
}
