package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

interface RObject : Comparable<RObject> {

    operator fun plus(other: RObject): RObject =
        throw RapiraInvalidOperationError(Operation.Addition)

    operator fun minus(other: RObject): OperationResult =
        OperationResult.Error("Illegal subtraction operation")

    fun negate(): OperationResult =
        OperationResult.Error("Illegal negation operation")

    operator fun times(other: RObject): OperationResult =
        OperationResult.Error("Illegal multiplication operation")

    operator fun div(other: RObject): OperationResult =
        OperationResult.Error("Illegal division operation")

    fun intDivide(other: RObject): OperationResult =
        OperationResult.Error("Illegal integer division operation")

    operator fun rem(other: RObject): OperationResult =
        OperationResult.Error("Illegal modulo operation")

    fun power(other: RObject): OperationResult =
        OperationResult.Error("Illegal exponent operation")

    fun length(): OperationResult =
        OperationResult.Error("Illegal length operation")

    infix fun and(other: RObject): OperationResult =
        OperationResult.Error("Illegal and operation")

    infix fun or(other: RObject): OperationResult =
        OperationResult.Error("Illegal or operation")

    fun not(): OperationResult =
        OperationResult.Error("Illegal not operation")

    fun elementAt(other: RObject): OperationResult =
        OperationResult.Error("Illegal indexing operation")

    fun slice(start: RObject?, end: RObject?): OperationResult =
        OperationResult.Error("Illegal slice operation")

    override fun compareTo(other: RObject): Int =
        throw RapiraInvalidOperationError("Cannot compare objects")
}
