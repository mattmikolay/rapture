package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

abstract class RObject(val typeDescription: String) : Comparable<RObject> {

    open operator fun plus(other: RObject): RObject = throw RapiraInvalidOperationError(Operation.Addition, this)

    open operator fun minus(other: RObject): RObject =
        throw RapiraInvalidOperationError(Operation.Subtraction, this)

    open fun negate(): RObject = throw RapiraInvalidOperationError(Operation.Negation, this)

    open operator fun times(other: RObject): RObject =
        throw RapiraInvalidOperationError(Operation.Multiplication, this)

    open operator fun div(other: RObject): RObject = throw RapiraInvalidOperationError(Operation.Division, this)

    open fun intDivide(other: RObject): RObject =
        throw RapiraInvalidOperationError(Operation.IntDivision, this)

    open operator fun rem(other: RObject): RObject = throw RapiraInvalidOperationError(Operation.Modulo, this)

    open fun power(other: RObject): OperationResult =
        OperationResult.Error("Illegal exponent operation")

    open fun length(): RObject =
        throw RapiraInvalidOperationError(Operation.Length, this)

    open infix fun and(other: RObject): OperationResult =
        OperationResult.Error("Illegal and operation")

    open infix fun or(other: RObject): OperationResult =
        OperationResult.Error("Illegal or operation")

    open fun not(): OperationResult =
        OperationResult.Error("Illegal not operation")

    open fun elementAt(other: RObject): RObject =
        throw RapiraInvalidOperationError(Operation.ElementAt, this)

    open fun slice(start: RObject?, end: RObject?): RObject =
        throw RapiraInvalidOperationError(Operation.Slice, this)

    override fun compareTo(other: RObject): Int =
        throw RapiraInvalidOperationError("Cannot compare objects")
}
