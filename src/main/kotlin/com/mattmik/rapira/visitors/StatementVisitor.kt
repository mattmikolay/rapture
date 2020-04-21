package com.mattmik.rapira.visitors

import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser

class StatementVisitor : RapiraLangBaseVisitor<Unit>() {

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
