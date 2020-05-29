package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.andThen
import com.mattmik.rapira.variables.IndexedVariable
import com.mattmik.rapira.variables.SliceVariable
import com.mattmik.rapira.variables.Variable

/**
 * A visitor that constructs a [Variable] while walking the tree within a given
 * [environment].
 */
class VariableVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Variable>() {

    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitVariableCommaIndex(ctx: RapiraLangParser.VariableCommaIndexContext): Variable {
        val variable = visit(ctx.variable())

        return ctx.commaExpression().expression()
            .map {
                expressionVisitor.visit(it) as? RInteger
                    ?: throw InvalidOperationError("Value is not a valid index", token = it.start)
            }
            .fold(variable) { resultVariable, index -> IndexedVariable(resultVariable, index.value) }
    }

    override fun visitVariableColonIndex(ctx: RapiraLangParser.VariableColonIndexContext): Variable {
        val variable = visit(ctx.variable())

        val leftExpr = ctx.leftExpr?.let { expressionVisitor.visit(it) }
        val rightExpr = ctx.rightExpr?.let { expressionVisitor.visit(it) }

        val startIndex = leftExpr ?: RInteger(1)
        val endIndex = rightExpr ?: (variable.getValue().andThen { it.length() } as? Result.Success)?.obj
            ?: throw InvalidOperationError("Cannot access index of object", token = ctx.variable().start)

        return SliceVariable(variable, startIndex, endIndex)
    }

    override fun visitVariableIdentifier(ctx: RapiraLangParser.VariableIdentifierContext) =
        environment[ctx.IDENTIFIER().text]
}
