package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.RapiraObject

// TODO: Add support for index expressions
class InOutArgument(private val variable: RapiraLangParser.VariableContext) : Argument {
    override fun evaluate(environment: Environment): RapiraObject {
        return environment[variable.IDENTIFIER().text]
    }
}