package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

data class Logical(val value: Boolean) : RObject("logical") {

    override fun and(other: RObject) = when (other) {
        is Logical -> Logical(value && other.value)
        else -> throw RapiraInvalidOperationError(Operation.And, other)
    }

    override fun or(other: RObject) = when (other) {
        is Logical -> Logical(value || other.value)
        else -> throw RapiraInvalidOperationError(Operation.Or, other)
    }

    override fun not() = Logical(!value)

    override fun toString() = if (value) "yes" else "no"
}

val LogicalYes = Logical(true)
val LogicalNo = Logical(false)
