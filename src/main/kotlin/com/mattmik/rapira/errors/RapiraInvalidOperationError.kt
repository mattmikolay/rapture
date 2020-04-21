package com.mattmik.rapira.errors

import com.mattmik.rapira.objects.RapiraObject

enum class Operation(val description: String) {
    Addition("addition"),
    Subtraction("subtraction"),
    Negation("negation"),
    Multiplication("multiplication"),
    Division("division"),
    IntDivision("integer division"),
    Modulo("modulo"),
    Exponentiation("exponentiation"),
}

class RapiraInvalidOperationError(
    operation: Operation,
    rootCauseObject: RapiraObject
) : RapiraRuntimeError("cannot perform ${operation.description} using object of type ${rootCauseObject.typeDescription}")
