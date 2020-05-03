package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.variables.IndexedVariable
import com.mattmik.rapira.variables.Variable

class VariableVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Variable>() {
    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitVariable(ctx: RapiraLangParser.VariableContext): Variable {
        var variable = environment[ctx.IDENTIFIER().text]

        // Flatten comma expressions
        ctx.indexExpression()
            .flatMap { it.commaExpression().expression() }
            .forEach { expr ->
                variable = IndexedVariable(variable, expressionVisitor.visit(expr))
            }

        // TODO Support colon index expressions

        return variable
    }
}
