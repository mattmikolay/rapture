package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

abstract class RObject(val typeDescription: String) : Comparable<RObject> {

    open operator fun plus(other: RObject): RObject = throw RapiraInvalidOperationError(Operation.Addition, this)

    open operator fun minus(other: RObject): RObject =
        throw RapiraInvalidOperationError(Operation.Subtraction, this)

    open fun negate(): OperationResult =
        OperationResult.Error("Illegal negation operation")

    open operator fun times(other: RObject): OperationResult =
        OperationResult.Error("Illegal multiplication operation")

    open operator fun div(other: RObject): OperationResult =
        OperationResult.Error("Illegal division operation")

    open fun intDivide(other: RObject): OperationResult =
        OperationResult.Error("Illegal integer division operation")

    open operator fun rem(other: RObject): OperationResult =
        OperationResult.Error("Illegal modulo operation")

    open fun power(other: RObject): OperationResult =
        OperationResult.Error("Illegal exponent operation")

    open fun length(): OperationResult =
        OperationResult.Error("Illegal length operation")

    open infix fun and(other: RObject): OperationResult =
        OperationResult.Error("Illegal and operation")

    open infix fun or(other: RObject): OperationResult =
        OperationResult.Error("Illegal or operation")

    open fun not(): OperationResult =
        OperationResult.Error("Illegal not operation")

    open fun elementAt(other: RObject): OperationResult =
        OperationResult.Error("Illegal indexing operation")

    open fun slice(start: RObject?, end: RObject?): RObject =
        throw RapiraInvalidOperationError(Operation.Slice, this)

    override fun compareTo(other: RObject): Int =
        throw RapiraInvalidOperationError("Cannot compare objects")
}
