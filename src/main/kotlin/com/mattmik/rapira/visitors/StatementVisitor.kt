package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RapiraCallable
import com.mattmik.rapira.objects.RapiraLogical
import com.mattmik.rapira.objects.RapiraObject
import com.mattmik.rapira.objects.formatRapiraObject

class StatementVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Unit>() {
    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext) {
        val procedure = ExpressionVisitor(environment).visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text] = procedure
        }
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext) {
        val function = ExpressionVisitor(environment).visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text] = function
        }
    }

    override fun visitAssignStatement(ctx: RapiraLangParser.AssignStatementContext) {
        // TODO Handle index expressions
        val variableName = ctx.IDENTIFIER()
        val evaluatedExpression = ExpressionVisitor(environment).visit(ctx.expression())
        environment[variableName.text] = evaluatedExpression
    }

    override fun visitCallStatement(ctx: RapiraLangParser.CallStatementContext) {
        val expressionVisitor = ExpressionVisitor(environment)
        val callable = ctx.IDENTIFIER()?.let { environment[it.text] }
            ?: expressionVisitor.visit(ctx.expression())

        // TODO Add support for in-out params
        val arguments = ctx.procedureArguments()?.expression()?.map { expressionVisitor.visit(it) }
            ?: emptyList<RapiraObject>()

        when (callable) {
            is RapiraCallable -> callable.call(environment, arguments)
            else -> throw RapiraInvalidOperationError("Cannot invoke object that is neither a procedure nor function")
        }
    }

    override fun visitIfStatement(ctx: RapiraLangParser.IfStatementContext) {
        val conditionResult = ExpressionVisitor(environment).visit(ctx.condition)
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
        val expressionVisitor = ExpressionVisitor(environment)
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

    // Expression statements are only valid in the REPL
    override fun visitExpressionStatement(ctx: RapiraLangParser.ExpressionStatementContext) {
        val expressionVisitor = ExpressionVisitor(environment)
        val expressionResult = expressionVisitor.visit(ctx.expression())
        println(expressionResult)
    }
}
