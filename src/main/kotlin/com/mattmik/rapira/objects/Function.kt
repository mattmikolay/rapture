package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.variables.ReadOnlyVariable

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

    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        arguments.forEach { arg ->
            arg as? InArgument
                ?: throw RapiraIllegalArgumentException("Cannot pass in-out argument to function call", arg)
        }

        val newEnvironment = Environment(environment)
        name?.let {
            newEnvironment[it] = ReadOnlyVariable(this)
        }

        return callable.call(newEnvironment, arguments)
    }

    override fun toString() = "function"
}
