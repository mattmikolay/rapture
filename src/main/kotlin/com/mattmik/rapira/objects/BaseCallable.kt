package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.errors.IncorrectArgumentCountError
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.visitors.StatementVisitor
import org.antlr.v4.runtime.Token

class BaseCallable(
    private val statements: RapiraLangParser.StmtsContext?,
    private val params: List<Parameter>,
    private val extern: List<String>
) : RCallable {

    override fun call(
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ): RObject? {
        if (params.size != arguments.size) {
            throw IncorrectArgumentCountError(
                expectedArgCount = params.size,
                actualArgCount = arguments.size,
                token = callToken
            )
        }

        val newEnvironment = makeNewEnvironment(environment, arguments)

        statements?.let {
            StatementVisitor(newEnvironment).visit(it)
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
