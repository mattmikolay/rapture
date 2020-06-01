package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

class InvalidOperationError(cause: String, token: Token) : InterpreterRuntimeError(cause, token)
