package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.errors.IncorrectArgumentCountError
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.visitors.StatementVisitor

class BaseCallable(
    private val statements: RapiraLangParser.StmtsContext?,
    private val params: List<Parameter>,
    private val extern: List<String>
) : RCallable {

    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        if (params.size != arguments.size) {
            throw IncorrectArgumentCountError(
                expectedArgCount = params.size,
                actualArgCount = arguments.size
            )
        }

        val newEnvironment = makeNewEnvironment(environment, arguments)

        try {
            statements?.let {
                StatementVisitor(newEnvironment).visit(it)
            }
        } catch (exception: CallableReturnException) {
            return exception.returnValue
        }

        return null
    }

    private fun makeNewEnvironment(environment: Environment, arguments: List<Argument>): Environment {
        val newEnvironment = Environment()

        params.zip(arguments).forEach { (param, argument) ->
            when (argument) {
                is InArgument -> if (param.type != ParamType.In)
                    throw IllegalArgumentError("Unexpected in argument passed to in-out param", argument)
                else -> if (param.type != ParamType.InOut)
                    throw IllegalArgumentError("Unexpected in-out argument passed to in param", argument)
            }

            newEnvironment[param.name] = argument.evaluate(environment)
        }

        extern.map { Pair(it, environment[it]) }
            .forEach { (name, variable) -> newEnvironment[name] = variable }

        return newEnvironment
    }
}
