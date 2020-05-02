package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.control.ProcedureReturnException
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.visitors.StatementVisitor

class Procedure(
    private val bodyStatements: RapiraLangParser.StmtsContext? = null,
    private val params: List<String> = emptyList(),
    private val extern: List<String> = emptyList()
) : RObject("procedure"), RapiraCallable {
    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        if (params.size != arguments.size) {
            throw RapiraInvalidOperationError("Number of params does not match number of arguments")
        }

        val newEnvironment = Environment()
        params.zip(arguments).forEach { (paramName, argument) ->
            newEnvironment[paramName] = argument.evaluate(environment)
        }
        extern.map { Pair(it, environment[it]) }
            .forEach { (name, variable) -> newEnvironment[name] = variable }

        var returnValue: RObject? = null

        try {
            bodyStatements?.let { StatementVisitor(newEnvironment).visit(it) }
        } catch (exception: ProcedureReturnException) {
            returnValue = exception.returnValue
        }

        return returnValue
    }

    override fun toString() = "procedure"
}
