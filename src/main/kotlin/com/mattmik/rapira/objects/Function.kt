package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.variables.ReadOnlyVariable

class Function(
    private val functionName: String?,
    private val procedure: Procedure
) : RObject("function"), RCallable by procedure {

    constructor(
        functionName: String? = null,
        bodyStatements: RapiraLangParser.StmtsContext? = null,
        params: List<Parameter> = emptyList(),
        extern: List<String> = emptyList()
    ) : this(
        functionName,
        Procedure(
            null,
            bodyStatements,
            params,
            if (functionName != null) (extern + functionName) else extern
        )
    )

    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        if (arguments.any { it is InOutArgument }) {
            throw RapiraIllegalArgumentException("Cannot pass in-out argument to function call")
        }

        val newEnvironment = Environment(environment)
        functionName?.let {
            newEnvironment[it] = ReadOnlyVariable(this)
        }

        return procedure.call(newEnvironment, arguments)
    }

    override fun toString() = "function"
}
