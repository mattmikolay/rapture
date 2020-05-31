package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
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
        val newEnvironment = Environment(environment)
        name?.let {
            newEnvironment[it] = ReadOnlyVariable(this)
        }

        val returnValue = callable.call(environment, arguments, callToken)
        if (returnValue != null) {
            // TODO Add token
            throw InvalidOperationError("Cannot return value within procedure")
        }

        return null
    }

    override fun toString() = "procedure"
}
