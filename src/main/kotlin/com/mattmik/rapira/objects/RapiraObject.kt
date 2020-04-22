package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.Operation
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

abstract class RapiraObject(val typeDescription: String) {

    open fun add(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Addition, this)

    open fun subtract(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Subtraction, this)

    open fun negate(): RapiraObject = throw RapiraInvalidOperationError(Operation.Negation, this)

    open fun multiply(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Multiplication, this)

    open fun divide(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Division, this)

    open fun intDivide(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.IntDivision, this)

    open fun modulus(other: RapiraObject): RapiraObject = throw RapiraInvalidOperationError(Operation.Modulo, this)

    open fun power(other: RapiraObject): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Exponentiation, this)

    open fun length(): RapiraObject =
        throw RapiraInvalidOperationError(Operation.Length, this)
}
