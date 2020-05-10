package com.mattmik.rapira.errors

import com.mattmik.rapira.objects.RObject

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
    And("logical and operation"),
    Or("logical or operation"),
    Not("logical not operation"),
    ElementAt("element at operation"),
    Slice("slice")
}

class RapiraInvalidOperationError(cause: String) : RapiraRuntimeError(cause) {
    constructor(
        operation: Operation,
        rootCauseObject: RObject
    ) : this("cannot perform ${operation.description} using object of type ${rootCauseObject.typeDescription}")
}
