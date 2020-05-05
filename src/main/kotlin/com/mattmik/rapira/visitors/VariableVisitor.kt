package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.variables.IndexedVariable
import com.mattmik.rapira.variables.SliceVariable
import com.mattmik.rapira.variables.Variable

class VariableVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Variable>() {
    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitVariable(ctx: RapiraLangParser.VariableContext): Variable {
        var variable = environment[ctx.IDENTIFIER().text]

        // Flatten comma expressions
        ctx.indexExpression().forEach { indexExprContext ->

            indexExprContext.commaExpression()?.let {
                it.expression().forEach { expr ->
                    variable = IndexedVariable(variable, expressionVisitor.visit(expr))
                }
            }

            val leftIndex = indexExprContext.leftIndex?.let { expressionVisitor.visit(it) }
            val rightIndex = indexExprContext.rightIndex?.let { expressionVisitor.visit(it) }
            if (leftIndex != null || rightIndex != null) {
                variable = SliceVariable(
                    variable,
                    startIndex = leftIndex ?: RInteger(1),
                    endIndex = rightIndex ?: variable.value.length()
                )
            }
        }

        return variable
    }
}
