package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.control.ProcedureReturnException
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.RapiraCallable
import com.mattmik.rapira.objects.formatRObject

class StatementVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Unit>() {

    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext) {
        val procedure = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text] = procedure
        }
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext) {
        val function = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text] = function
        }
    }

    override fun visitAssignStatement(ctx: RapiraLangParser.AssignStatementContext) {
        // TODO Handle index expressions
        val variableName = ctx.IDENTIFIER()
        val evaluatedExpression = expressionVisitor.visit(ctx.expression())
        environment[variableName.text] = evaluatedExpression
    }

    override fun visitCallStatement(ctx: RapiraLangParser.CallStatementContext) {
        val callable = ctx.IDENTIFIER()?.let { environment[it.text] }
            ?: expressionVisitor.visit(ctx.expression())

        val arguments = readProcedureArguments(ctx.procedureArguments())

        when (callable) {
            is RapiraCallable -> callable.call(environment, arguments)
            else -> throw RapiraInvalidOperationError("Cannot invoke object that is neither a procedure nor function")
        }
    }

    override fun visitIfStatement(ctx: RapiraLangParser.IfStatementContext) {
        val conditionResult = expressionVisitor.visit(ctx.condition)
        if (conditionResult == Logical(true)) {
            visit(ctx.ifBody)
        } else ctx.elseBody?.let {
            visit(it)
        }
    }

    override fun visitConditionCaseStatement(ctx: RapiraLangParser.ConditionCaseStatementContext?) {
        super.visitConditionCaseStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitConditionlessCaseStatement(ctx: RapiraLangParser.ConditionlessCaseStatementContext) {
        for (whenClause in ctx.singleWhenClause()) {
            val whenResult = expressionVisitor.visit(whenClause.expression())
            if (whenResult == Logical(true)) {
                visit(whenClause.stmts())
                return
            }
        }

        ctx.elseBody?.let { visit(it) }
    }

    override fun visitLoopStatement(ctx: RapiraLangParser.LoopStatementContext) {
        // TODO Not fully implemented!
        try {
            visit(ctx.stmts())
        } catch (exception: LoopExitException) {
            // no-op
        }
    }

    override fun visitOutputStatement(ctx: RapiraLangParser.OutputStatementContext) {
        val expressionResults = ctx.expression().map { expression -> expressionVisitor.visit(expression) }
        val formattedOutput = expressionResults.joinToString(
            separator = " ",
            postfix = if (ctx.nlf === null) System.lineSeparator() else "",
            transform = { obj -> formatRObject(obj) }
        )
        print(formattedOutput)
    }

    override fun visitInputStatement(ctx: RapiraLangParser.InputStatementContext?) {
        super.visitInputStatement(ctx)
        TODO("Not yet implemented")
    }

    override fun visitExitStatement(ctx: RapiraLangParser.ExitStatementContext) =
        throw LoopExitException()

    override fun visitReturnStatement(ctx: RapiraLangParser.ReturnStatementContext) {
        val returnValue = ctx.expression()?.let {
            expressionVisitor.visit(it)
        }
        throw ProcedureReturnException(returnValue)
    }

    // Expression statements are only valid in the REPL
    override fun visitExpressionStatement(ctx: RapiraLangParser.ExpressionStatementContext) {
        val expressionResult = expressionVisitor.visit(ctx.expression())
        println(expressionResult)
    }

    private fun readProcedureArguments(ctx: RapiraLangParser.ProcedureArgumentsContext): List<Argument> {
        return ctx.procedureArgument().map {
            it.expression()?.let { expr -> InArgument(expr) }
                ?: it.variable()?.let { variable -> InOutArgument(variable) }
                ?: throw RapiraInvalidOperationError("Invalid argument type")
        }
    }
}
