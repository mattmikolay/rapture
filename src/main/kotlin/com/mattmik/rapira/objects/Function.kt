package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.variables.ReadOnlyVariable
import org.antlr.v4.runtime.Token

class Function private constructor(
    private val name: String?,
    private val callable: RCallable
) : RObject, RCallable by callable {

    constructor(
        name: String? = null,
        statements: RapiraLangParser.StmtsContext? = null,
        params: List<Parameter> = emptyList(),
        extern: List<String> = emptyList()
    ) : this(
        name,
        BaseCallable(
            statements,
            params,
            if (name != null) (extern + name) else extern
        )
    )

    override fun call(
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ): RObject? {
        arguments.forEach { arg ->
            arg as? InArgument
                ?: throw IllegalArgumentError("Cannot pass in-out argument to function call", arg)
        }

        val newEnvironment = Environment(environment)
        name?.let {
            newEnvironment[it] = ReadOnlyVariable(this)
        }

        return try {
            callable.call(newEnvironment, arguments, callToken)
        } catch (exception: CallableReturnException) {
            exception.returnValue
        }
    }

    override fun toString() = "function"
}
