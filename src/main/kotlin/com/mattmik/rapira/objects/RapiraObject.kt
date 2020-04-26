package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError

abstract class RapiraObject(val typeDescription: String) {

    open operator fun plus(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Addition, this)

    open operator fun minus(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Subtraction, this)

    open fun negate(): RapiraObject = throw RapiraInvalidOperationError(Operation.Negation, this)

    open operator fun times(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Multiplication, this)

    open operator fun div(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Division, this)

    open fun intDivide(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.IntDivision, this)

    open operator fun rem(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Modulo, this)

    open fun power(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Exponentiation, this)

    open fun length(): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Length, this)

    open infix fun lessThan(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.LessThan, this)

    open infix fun greaterThan(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.GreaterThan, this)

    open infix fun lessThanEqualTo(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.LessThanEqualTo, this)

    open fun greaterThanEqualTo(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.GreaterThanEqualTo, this)

    open infix fun and(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.And, this)

    open infix fun or(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Or, this)

    open fun not(): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Not, this)

    open fun elementAt(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.ElementAt, this)
}
