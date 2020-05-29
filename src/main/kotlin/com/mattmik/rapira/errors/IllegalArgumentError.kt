package com.mattmik.rapira.errors

import com.mattmik.rapira.args.Argument

class IllegalArgumentError(message: String, argument: Argument) :
    InterpreterRuntimeError(message, token = argument.token)
