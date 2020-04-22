package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class RapiraLogical(val value: Boolean) : RapiraObject("logical") {

    override fun and(other: RapiraObject) = when (other) {
        is RapiraLogical -> RapiraLogical(value && other.value)
        else -> throw RapiraInvalidOperationError(Operation.And, other)
    }

    override fun or(other: RapiraObject) = when (other) {
        is RapiraLogical -> RapiraLogical(value || other.value)
        else -> throw RapiraInvalidOperationError(Operation.Or, other)
    }

    override fun not() = RapiraLogical(!value)

    override fun toString() = if (value) "yes" else "no"
}
