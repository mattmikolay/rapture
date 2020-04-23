package com.mattmik.rapira.visitors

import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.RapiraLogical
import com.mattmik.rapira.objects.formatRapiraObject

class StatementVisitor : RapiraLangBaseVisitor<Unit>() {

    override fun visitAssignStatement(ctx: RapiraLangParser.AssignStatementContext?) {
        super.visitAssignStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitCallStatement(ctx: RapiraLangParser.CallStatementContext?) {
        super.visitCallStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitIfStatement(ctx: RapiraLangParser.IfStatementContext) {
        val conditionResult = ExpressionVisitor().visit(ctx.condition)
        if (conditionResult == RapiraLogical(true)) {
            visit(ctx.ifBody)
        } else ctx.elseBody?.let {
            visit(it)
        }
    }

    override fun visitCaseStatement(ctx: RapiraLangParser.CaseStatementContext?) {
        super.visitCaseStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitLoopStatement(ctx: RapiraLangParser.LoopStatementContext?) {
        super.visitLoopStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitOutputStatement(ctx: RapiraLangParser.OutputStatementContext) {
        val expressionVisitor = ExpressionVisitor()
        val expressionResults = ctx.expression().map { expression -> expressionVisitor.visit(expression) }
        val formattedOutput = expressionResults.joinToString(
            separator = " ",
            postfix = if (ctx.nlf === null) System.lineSeparator() else "",
            transform = { obj -> formatRapiraObject(obj) }
        )
        print(formattedOutput)
    }

    override fun visitInputStatement(ctx: RapiraLangParser.InputStatementContext?) {
        super.visitInputStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitExitStatement(ctx: RapiraLangParser.ExitStatementContext?) {
        super.visitExitStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitReturnStatement(ctx: RapiraLangParser.ReturnStatementContext?) {
        super.visitReturnStatement(ctx)
        TODO("Not yet implemented")
    }
}
