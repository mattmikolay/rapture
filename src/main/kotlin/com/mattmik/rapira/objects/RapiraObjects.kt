package com.mattmik.rapira.objects

sealed class RapiraObject {
    abstract fun add(other: RapiraObject): RapiraObject
    abstract fun subtract(other: RapiraObject): RapiraObject
    abstract fun negate(): RapiraObject
}

object RapiraEmpty : RapiraObject() {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> TODO("Not yet implemented")
        is RapiraEmpty -> TODO("Not yet implemented")
    }

    override fun subtract(other: RapiraObject) = when (other) {
        is RapiraInteger -> TODO("Not yet implemented")
        is RapiraEmpty -> TODO("Not yet implemented")
    }

    override fun negate(): RapiraObject {
        TODO("Not yet implemented")
    }
}

data class RapiraInteger(val value: Int) : RapiraObject() {
    override fun add(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value + other.value)
        is RapiraEmpty -> TODO("Not yet implemented")
    }

    override fun subtract(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value - other.value)
        is RapiraEmpty -> TODO("Not yet implemented")
    }

    override fun negate(): RapiraObject = RapiraInteger(-value)
}
