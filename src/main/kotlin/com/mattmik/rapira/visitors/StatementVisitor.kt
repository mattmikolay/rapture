package com.mattmik.rapira.visitors

import com.mattmik.rapira.ConsoleReader
import com.mattmik.rapira.ConsoleWriter
import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.control.ForLoopController
import com.mattmik.rapira.control.LoopController
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.control.MasterLoopController
import com.mattmik.rapira.control.RepeatLoopController
import com.mattmik.rapira.control.WhileLoopController
import com.mattmik.rapira.errors.RapiraIllegalInvocationError
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.LogicalYes
import com.mattmik.rapira.objects.RCallable
import com.mattmik.rapira.util.getOrThrow

/**
 * A visitor that executes statements while walking the tree within a given [environment].
 */
class StatementVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Unit>() {

    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext) {
        val procedure = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].setValue(procedure)
                .getOrThrow { reason -> RapiraInvalidOperationError(reason, it.symbol) }
        }
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext) {
        val function = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].setValue(function)
                .getOrThrow { reason -> RapiraInvalidOperationError(reason, it.symbol) }
        }
    }

    override fun visitAssignStatement(ctx: RapiraLangParser.AssignStatementContext) {
        val evaluatedExpression = expressionVisitor.visit(ctx.expression())
        val variable = VariableVisitor(environment).visit(ctx.variable())
        variable.setValue(evaluatedExpression)
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
    }

    override fun visitCallStatement(ctx: RapiraLangParser.CallStatementContext) {
        val obj = ctx.IDENTIFIER()?.let {
            environment[it.text].getValue()
                .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = it.symbol) }
        }
            ?: expressionVisitor.visit(ctx.expression())

        val callable = obj as? RCallable
            ?: throw RapiraIllegalInvocationError(
                token = ctx.CALL()?.symbol ?: ctx.procedureArguments().LPAREN().symbol
            )

        val arguments = readProcedureArguments(ctx.procedureArguments())

        callable.call(environment, arguments)
    }

    override fun visitIfStatement(ctx: RapiraLangParser.IfStatementContext) {
        val conditionResult = expressionVisitor.visit(ctx.condition)
        if (conditionResult == Logical(true)) {
            visit(ctx.ifBody)
        } else ctx.elseBody?.let {
            visit(it)
        }
    }

    override fun visitConditionCaseStatement(ctx: RapiraLangParser.ConditionCaseStatementContext) {
        val conditionResult = expressionVisitor.visit(ctx.condition)

        val whenClauses = ctx.multiWhenClause().flatMap { multiWhenClause ->
            multiWhenClause.expression().map { expr -> Pair(expr, multiWhenClause.stmts()) }
        }
        for (whenClause in whenClauses) {
            val whenResult = expressionVisitor.visit(whenClause.first)
            if (whenResult == conditionResult) {
                visit(whenClause.second)
                return
            }
        }

        ctx.elseBody?.let { visit(it) }
    }

    override fun visitConditionlessCaseStatement(ctx: RapiraLangParser.ConditionlessCaseStatementContext) {
        for (whenClause in ctx.singleWhenClause()) {
            val whenResult = expressionVisitor.visit(whenClause.expression())
            if (whenResult == LogicalYes) {
                visit(whenClause.stmts())
                return
            }
        }

        ctx.elseBody?.let { visit(it) }
    }

    override fun visitLoopStatement(ctx: RapiraLangParser.LoopStatementContext) {
        val allControllers = mutableListOf<LoopController>()

        ctx.repeatClause()?.expression()?.let {
            val repeatExprResult = expressionVisitor.visit(it)
            allControllers.add(RepeatLoopController(repeatExprResult))
        }

        ctx.forClause()?.let {
            allControllers.add(
                ForLoopController(
                    variable = environment[it.IDENTIFIER().text],
                    fromValue = it.fromExpr?.let { expr -> expressionVisitor.visit(expr) },
                    toValue = it.toExpr?.let { expr -> expressionVisitor.visit(expr) },
                    stepValue = it.stepExpr?.let { expr -> expressionVisitor.visit(expr) }
                )
            )
        }

        ctx.whileClause()?.let {
            allControllers.add(
                WhileLoopController(
                    condition = it.expression(),
                    expressionVisitor = expressionVisitor
                )
            )
        }

        val loopController = MasterLoopController(allControllers)

        while (loopController.isLoopActive()) {
            try {
                visit(ctx.stmts())
            } catch (exception: LoopExitException) {
                break
            }

            loopController.update()

            if (ctx.untilExpr?.let { expressionVisitor.visit(it) } == LogicalYes) {
                break
            }
        }
    }

    override fun visitOutputStatement(ctx: RapiraLangParser.OutputStatementContext) {
        val expressionResults = ctx.expression().map { expr -> expressionVisitor.visit(expr) }
        ConsoleWriter.printObjects(
            objects = expressionResults,
            lineBreak = ctx.nlf === null
        )
    }

    override fun visitInputStatement(ctx: RapiraLangParser.InputStatementContext) {
        val variableVisitor = VariableVisitor(environment)
        val isTextMode = ctx.inputMode?.type == RapiraLangParser.MODE_TEXT

        ctx.variable().forEach {
            val variable = variableVisitor.visit(it)
            variable.setValue(
                if (isTextMode)
                    ConsoleReader.readText()
                else
                    ConsoleReader.readObject()
            ).getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        }
    }

    override fun visitExitStatement(ctx: RapiraLangParser.ExitStatementContext) =
        throw LoopExitException(token = ctx.LOOP_EXIT().symbol)

    override fun visitReturnStatement(ctx: RapiraLangParser.ReturnStatementContext) {
        val returnValue = ctx.expression()?.let {
            expressionVisitor.visit(it)
        }
        throw CallableReturnException(returnValue, token = ctx.RETURN().symbol)
    }

    // Expression statements are only valid in the REPL
    override fun visitExpressionStatement(ctx: RapiraLangParser.ExpressionStatementContext) {
        val expressionResult = expressionVisitor.visit(ctx.expression()) ?: Empty
        ConsoleWriter.println(expressionResult.toString())
    }

    private fun readProcedureArguments(ctx: RapiraLangParser.ProcedureArgumentsContext) =
        ctx.procedureArgument().map { arg ->
            arg.expression()?.let { expr -> InArgument(expr) }
                ?: InOutArgument(arg.variable())
        }
}
