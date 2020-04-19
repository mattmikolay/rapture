package com.mattmik.rapira.objects

sealed class RapiraObject {
    abstract fun add(other: RapiraObject): RapiraObject
    abstract fun subtract(other: RapiraObject): RapiraObject
}

data class RapiraInteger(val value: Int) : RapiraObject() {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value + other.value)
    }

    override fun subtract(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value - other.value)
    }
}
