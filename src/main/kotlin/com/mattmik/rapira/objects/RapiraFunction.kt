package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.visitors.StatementVisitor

// TODO: Add params and intern/extern declarations
class RapiraFunction(
    private val bodyStatements: RapiraLangParser.StmtsContext? = null
) : RapiraObject("function"), RapiraCallable {
    override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
        // TODO Compute new environment by reading intern values from passed in environment and arguments
        val newEnvironment = Environment()

        StatementVisitor(newEnvironment).visit(bodyStatements)

        // TODO After execution, update previous environment using intern fields of this function

        // TODO Return any return value from function call
        return null
    }

    override fun toString() = "function"
}
