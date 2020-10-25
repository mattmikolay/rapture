package com.mattmik.rapira.objects

import com.mattmik.rapira.util.Result

/**
 * A Rapira object value.
 */
interface RObject {

    /**
     * A string representation of this object's type, used to generate error
     * messages. For example, "integer" or "text".
     */
    val typeName: String

    operator fun plus(other: RObject): Result<RObject> =
        Result.Error("Cannot add object of type ${other.typeName} to object of type $typeName")

    operator fun minus(other: RObject): Result<RObject> =
        Result.Error("Cannot subtract object of type ${other.typeName} from object of type $typeName")

    fun negate(): Result<RObject> =
        Result.Error("Cannot negate object of type $typeName")

    operator fun times(other: RObject): Result<RObject> =
        Result.Error("Cannot multiply object of type $typeName by object of type ${other.typeName}")

    operator fun div(other: RObject): Result<RObject> =
        Result.Error("Cannot divide object of type $typeName by object of type ${other.typeName}")

    fun intDivide(other: RObject): Result<RObject> =
        Result.Error("Cannot integer divide object of type $typeName by object of type ${other.typeName}")

    operator fun rem(other: RObject): Result<RObject> =
        Result.Error("Cannot perform modulus operation with objects of type $typeName and ${other.typeName}")

    fun power(other: RObject): Result<RObject> =
        Result.Error(
            "Cannot perform exponentiation operation with objects of type $typeName and ${other.typeName}"
        )

    fun length(): Result<RObject> =
        Result.Error("Object of type $typeName does not have a length")

    infix fun and(other: RObject): Result<RObject> =
        Result.Error("Cannot perform and operation with objects of type $typeName and ${other.typeName}")

    infix fun or(other: RObject): Result<RObject> =
        Result.Error("Cannot perform or operation with objects of type $typeName and ${other.typeName}")

    fun not(): Result<RObject> =
        Result.Error("Cannot perform not operation on object of type $typeName")

    fun elementAt(other: RObject): Result<RObject> =
        Result.Error("Object of type $typeName is not indexable")

    fun slice(start: RObject? = null, end: RObject? = null): Result<RObject> =
        Result.Error("Object of type $typeName is not indexable")

    fun compare(other: RObject): Result<Int> =
        Result.Error("Cannot compare object of type $typeName with object of type ${other.typeName}")
}
