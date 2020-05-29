package com.mattmik.rapira.errors

import com.mattmik.rapira.args.Argument

class RapiraIllegalArgumentException(message: String, argument: Argument) :
    RapiraRuntimeError(message, token = argument.token)
