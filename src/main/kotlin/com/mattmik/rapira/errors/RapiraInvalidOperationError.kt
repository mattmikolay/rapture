package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

enum class Operation(val description: String) {
    Addition("addition")
}

class RapiraInvalidOperationError(cause: String, token: Token? = null) : RapiraRuntimeError(cause, token) {
    constructor(operation: Operation) : this("cannot perform ${operation.description}")
}
