package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.errors.IllegalReturnValueError
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.variables.ReadOnlyVariable
import org.antlr.v4.runtime.Token

class Procedure private constructor(
    private val name: String?,
    private val callable: RCallable
) : RObject, RCallable by callable {

    constructor(
        name: String? = null,
        statements: RapiraParser.StmtsContext? = null,
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
        val newEnvironment = Environment(environment)
        name?.let {
            newEnvironment[it] = ReadOnlyVariable(this)
        }

        return try {
            callable.call(newEnvironment, arguments, callToken)
        } catch (exception: CallableReturnException) {
            if (exception.returnValue != null) {
                throw IllegalReturnValueError(token = exception.token)
            }
            null
        }
    }

    override fun toString() = "procedure"
}
