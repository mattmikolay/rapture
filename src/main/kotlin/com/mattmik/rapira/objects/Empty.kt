package com.mattmik.rapira.objects

/**
 * An empty Rapira object value.
 */
object Empty : RObject {

    override val typeName: String
        get() = "empty"

    override fun toString() = "empty"
}
