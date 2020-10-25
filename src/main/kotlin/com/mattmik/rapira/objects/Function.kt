package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.params.Parameter
import org.antlr.v4.runtime.Token

/**
 * A function Rapira object value. Functions can return values, but cannot be
 * passed in-out arguments.
 */
class Function(
    private val name: String? = null,
    statements: RapiraParser.StmtsContext? = null,
    params: List<Parameter> = emptyList(),
    extern: List<String> = emptyList()
) : Subroutine(name, statements, params, extern) {

    override val typeName: String
        get() = "function"

    override fun call(
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ): RObject? {
        arguments.forEach { arg ->
            arg as? InArgument
                ?: throw IllegalArgumentError("Cannot pass in-out argument to function call", arg)
        }

        return try {
            super.call(environment, arguments, callToken)
        } catch (exception: CallableReturnException) {
            exception.returnValue
        }
    }

    override fun toString() =
        name?.let { "fun[\"$it\"]" } ?: "fun"
}
