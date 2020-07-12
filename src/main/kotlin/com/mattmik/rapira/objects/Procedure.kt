package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.errors.IllegalReturnValueError
import com.mattmik.rapira.params.Parameter
import org.antlr.v4.runtime.Token

/**
 * A procedure Rapira object value. Procedures cannot return values, but can be
 * passed in-out arguments.
 */
class Procedure(
    name: String? = null,
    statements: RapiraParser.StmtsContext? = null,
    params: List<Parameter> = emptyList(),
    extern: List<String> = emptyList()
) : Subroutine(name, statements, params, extern) {

    override fun call(
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ): RObject? =
        try {
            super.call(environment, arguments, callToken)
        } catch (exception: CallableReturnException) {
            if (exception.returnValue != null) {
                throw IllegalReturnValueError(token = exception.token)
            }
            null
        }

    override fun toString() = "procedure"
}
