package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.variables.SimpleVariable
import com.mattmik.rapira.variables.Variable
import com.mattmik.rapira.visitors.ExpressionVisitor
import org.antlr.v4.runtime.Token

/**
 * An "in" argument. In arguments are passed by value.
 */
class InArgument(private val expressionContext: RapiraLangParser.ExpressionContext) : Argument {

    override val token: Token
        get() = expressionContext.start

    override fun evaluate(environment: Environment): Variable =
        SimpleVariable(
            ExpressionVisitor(environment).visit(expressionContext)
        )
}
