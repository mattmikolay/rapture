package com.mattmik.rapira.objects

data class RapiraLogical(val value: Boolean) : RapiraObject("logical") {
    // TODO Implement operations

    override fun toString() = if (value) "yes" else "no"
}
