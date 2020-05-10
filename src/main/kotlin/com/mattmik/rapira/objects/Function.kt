package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException

class Function(
    private val procedure: Procedure
) : RObject("function"), RCallable by procedure {

    constructor(
        functionName: String? = null,
        bodyStatements: RapiraLangParser.StmtsContext? = null,
        params: List<Parameter> = emptyList(),
        extern: List<String> = emptyList()
    ) : this(Procedure(functionName, bodyStatements, params, extern))

    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        if (arguments.any { it is InOutArgument }) {
            throw RapiraIllegalArgumentException("Cannot pass in-out argument to function call")
        }

        return procedure.call(environment, arguments)
    }

    override fun toString() = "function"
}
