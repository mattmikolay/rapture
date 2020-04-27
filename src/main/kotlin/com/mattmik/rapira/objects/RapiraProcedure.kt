package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.visitors.StatementVisitor

// TODO: Add support for intern/extern
class RapiraProcedure(
    private val bodyStatements: RapiraLangParser.StmtsContext? = null,
    private val params: List<String> = emptyList()
) : RapiraObject("procedure"), RapiraCallable {
    override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
        if (params.size != arguments.size) {
            throw RapiraInvalidOperationError("Number of params does not match number of arguments")
        }

        // TODO Compute new environment by reading intern values from passed in environment
        val newEnvironment = Environment()
        params.zip(arguments).forEach { (paramName, argument) ->
            newEnvironment.setObject(paramName, argument)
        }

        StatementVisitor(newEnvironment).visit(bodyStatements)

        // TODO After execution, update previous environment using intern fields of this function

        // TODO Return any return value from function call
        return null
    }

    override fun toString() = "procedure"
}
