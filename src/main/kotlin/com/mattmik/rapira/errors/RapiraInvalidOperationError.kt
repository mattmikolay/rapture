package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

class RapiraInvalidOperationError(cause: String, token: Token? = null) : RapiraRuntimeError(cause, token)
