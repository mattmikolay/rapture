package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.variables.Variable
import com.mattmik.rapira.visitors.VariableVisitor
import org.antlr.v4.runtime.Token

/**
 * An "in-out" argument. In-out arguments are passed by reference.
 */
class InOutArgument(private val variableContext: RapiraLangParser.VariableContext) : Argument {

    override val token: Token
        get() = variableContext.start

    override fun evaluate(environment: Environment): Variable =
        VariableVisitor(environment).visit(variableContext)
}
