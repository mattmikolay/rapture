package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.andThen
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
                    val index = (expressionVisitor.visit(expr) as? RInteger)?.value
                        ?: throw RapiraInvalidOperationError("Cannot use non-integer value as index", token = ctx.IDENTIFIER().symbol)
                    variable = IndexedVariable(variable, index)
                }
            }

            val leftIndex = indexExprContext.leftIndex?.let { expressionVisitor.visit(it) }
            val rightIndex = indexExprContext.rightIndex?.let { expressionVisitor.visit(it) }
            if (leftIndex != null || rightIndex != null) {
                val startIndex = leftIndex ?: RInteger(1)
                val endIndex = rightIndex ?: (variable.getValue().andThen { it.length() } as? Result.Success)?.obj
                    ?: throw RapiraInvalidOperationError("Cannot access index of object")

                variable = SliceVariable(variable, startIndex, endIndex)
            }
        }

        return variable
    }
}
