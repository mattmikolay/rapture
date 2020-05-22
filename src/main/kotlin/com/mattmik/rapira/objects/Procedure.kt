package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.variables.ReadOnlyVariable

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

    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        val newEnvironment = Environment(environment)
        name?.let {
            newEnvironment[it] = ReadOnlyVariable(this)
        }

        val returnValue = callable.call(environment, arguments)
        if (returnValue != null) {
            throw RapiraInvalidOperationError("Cannot return value within procedure")
        }

        return null
    }

    override fun toString() = "procedure"
}
