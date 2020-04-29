package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument

class RapiraFunction(
    private val procedure: RapiraProcedure
) : RapiraObject("function"), RapiraCallable by procedure {

    constructor(
        bodyStatements: RapiraLangParser.StmtsContext? = null,
        params: List<String> = emptyList(),
        extern: List<String> = emptyList()
    ) : this(RapiraProcedure(bodyStatements, params, extern))

    override fun call(environment: Environment, arguments: List<Argument>): RapiraObject? {
        // TODO Check arguments, throw error if any is in-out

        return procedure.call(environment, arguments)
    }

    override fun toString() = "function"
}
