package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.variables.ReadOnlyVariable

class Function private constructor(
    private val name: String?,
    private val callable: RCallable
) : RObject("function"), RCallable by callable {

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
        if (arguments.any { it is InOutArgument }) {
            throw RapiraIllegalArgumentException("Cannot pass in-out argument to function call")
        }

        val newEnvironment = Environment(environment)
        name?.let {
            newEnvironment[it] = ReadOnlyVariable(this)
        }

        return callable.call(newEnvironment, arguments)
    }

    override fun toString() = "function"
}
