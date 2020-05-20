package com.mattmik.rapira.objects

data class Logical(val value: Boolean) : RObject("logical") {

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

    override fun toString() = if (value) "yes" else "no"
}

val LogicalYes = Logical(true)
val LogicalNo = Logical(false)
