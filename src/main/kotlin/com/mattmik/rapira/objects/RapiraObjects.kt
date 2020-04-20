package com.mattmik.rapira.objects

sealed class RapiraObject {
    abstract fun add(other: RapiraObject): RapiraObject
    abstract fun subtract(other: RapiraObject): RapiraObject
    abstract fun negate(): RapiraObject
    abstract fun multiply(other: RapiraObject): RapiraObject
    abstract fun divide(other: RapiraObject): RapiraObject
    abstract fun intDivide(other: RapiraObject): RapiraObject
    abstract fun modulus(other: RapiraObject): RapiraObject
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

    override fun multiply(other: RapiraObject): RapiraObject {
        TODO("Not yet implemented")
    }

    override fun divide(other: RapiraObject): RapiraObject {
        TODO("Not yet implemented")
    }

    override fun intDivide(other: RapiraObject): RapiraObject {
        TODO("Not yet implemented")
    }

    override fun modulus(other: RapiraObject): RapiraObject {
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

    override fun multiply(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value * other.value)
        is RapiraEmpty -> TODO("Not yet implemented")
    }

    override fun divide(other: RapiraObject) = when (other) {
        is RapiraInteger -> TODO("Not yet implemented")
        is RapiraEmpty -> TODO("Not yet implemented")
    }

    override fun intDivide(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value / other.value)
        is RapiraEmpty -> TODO("Not yet implemented")
    }

    override fun modulus(other: RapiraObject) = when (other) {
        is RapiraInteger -> RapiraInteger(value % other.value)
        is RapiraEmpty -> TODO("Not yet implemented")
    }
}
