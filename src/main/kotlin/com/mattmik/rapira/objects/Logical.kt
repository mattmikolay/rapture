package com.mattmik.rapira.objects

import com.mattmik.rapira.util.toSuccess

data class Logical(val value: Boolean) : RObject {

    override fun and(other: RObject) = when (other) {
        is Logical -> Logical(value && other.value).toSuccess()
        else -> super.and(other)
    }

    override fun or(other: RObject) = when (other) {
        is Logical -> Logical(value || other.value).toSuccess()
        else -> super.or(other)
    }

    override fun not() =
        Logical(!value).toSuccess()

    override fun toString() =
        if (value) "yes" else "no"
}
