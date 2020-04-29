package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.RapiraObject
import com.mattmik.rapira.visitors.ExpressionVisitor

class InArgument(private val expression: RapiraLangParser.ExpressionContext) : Argument {

    override fun evaluate(environment: Environment): RapiraObject = ExpressionVisitor(environment).visit(expression)
}
