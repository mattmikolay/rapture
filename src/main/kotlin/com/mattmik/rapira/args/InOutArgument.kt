package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.variables.Variable
import com.mattmik.rapira.visitors.VariableVisitor

class InOutArgument(private val variableContext: RapiraLangParser.VariableContext) : Argument {

    override fun evaluate(environment: Environment): Variable =
        VariableVisitor(environment).visit(variableContext)
}
