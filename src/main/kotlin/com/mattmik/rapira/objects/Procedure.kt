package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.control.ProcedureReturnException
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.variables.ReadOnlyVariable
import com.mattmik.rapira.visitors.StatementVisitor

class Procedure(
    private val procedureName: String? = null,
    private val bodyStatements: RapiraLangParser.StmtsContext? = null,
    private val params: List<Parameter> = emptyList(),
    private val extern: List<String> = emptyList()
) : RObject("procedure"), RapiraCallable {
    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        if (params.size != arguments.size) {
            throw RapiraInvalidOperationError("Number of params does not match number of arguments")
        }

        val newEnvironment = Environment()

        // Allows for recursive calls
        if (procedureName != null) {
            // TODO This doesn't work for functions
            newEnvironment[procedureName] = ReadOnlyVariable(this)
        }

        params.zip(arguments).forEach { (param, argument) ->
            when (argument) {
                is InArgument -> if (param.type != ParamType.In)
                    throw RapiraIllegalArgumentException("Unexpected in argument passed to in-out param")
                else -> if (param.type != ParamType.InOut)
                    throw RapiraIllegalArgumentException("Unexpected in-out argument passed to in param")
            }

            newEnvironment[param.name] = argument.evaluate(environment)
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
