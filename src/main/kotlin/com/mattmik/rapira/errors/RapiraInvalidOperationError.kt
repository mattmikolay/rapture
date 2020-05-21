package com.mattmik.rapira.errors

import com.mattmik.rapira.objects.RObject
import org.antlr.v4.runtime.Token

enum class Operation(val description: String) {
    Addition("addition"),
    Subtraction("subtraction"),
    Multiplication("multiplication"),
    Slice("slice")
}

class RapiraInvalidOperationError(cause: String, token: Token? = null) : RapiraRuntimeError(cause, token) {
    constructor(
        operation: Operation,
        rootCauseObject: RObject
    ) : this("cannot perform ${operation.description} using object of type ${rootCauseObject.typeDescription}")
}
