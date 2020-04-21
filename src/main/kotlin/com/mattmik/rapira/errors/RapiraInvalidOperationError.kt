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
    Length("length operation"),
}

class RapiraInvalidOperationError(cause: String) : RapiraRuntimeError(cause) {
    constructor(
        operation: Operation,
        rootCauseObject: RapiraObject
    ) : this("cannot perform ${operation.description} using object of type ${rootCauseObject.typeDescription}")
}
