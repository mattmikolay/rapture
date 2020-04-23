package com.mattmik.rapira.visitors

import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.RapiraLogical

class StatementVisitor : RapiraLangBaseVisitor<Unit>() {

    override fun visitIfStatement(ctx: RapiraLangParser.IfStatementContext) {
        val conditionResult = ExpressionVisitor().visit(ctx.condition)
        if (conditionResult == RapiraLogical(true)) {
            visit(ctx.ifBody)
        } else ctx.elseBody?.let {
            visit(it)
        }
    }

    override fun visitOutputStatement(ctx: RapiraLangParser.OutputStatementContext) {
        val expressionVisitor = ExpressionVisitor()
        val expressionResults = ctx.expression().map { expression -> expressionVisitor.visit(expression) }
        val formattedOutput = expressionResults.joinToString(
            separator = " ",
            postfix = if (ctx.nlf === null) System.lineSeparator() else ""
        )
        print(formattedOutput)
    }
}
