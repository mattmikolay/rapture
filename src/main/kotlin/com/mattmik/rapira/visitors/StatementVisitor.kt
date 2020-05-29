package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.console.ConsoleReader
import com.mattmik.rapira.console.ConsoleWriter
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
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.util.getOrThrow

/**
 * A visitor that executes statements while walking the tree within a given
 * [environment].
 */
class StatementVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Unit>() {

    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext) {
        val procedure = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].setValue(procedure)
                .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = it.symbol) }
        }
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext) {
        val function = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].setValue(function)
                .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = it.symbol) }
        }
    }

    override fun visitAssignStatement(ctx: RapiraLangParser.AssignStatementContext) {
        val evaluatedExpression = expressionVisitor.visit(ctx.expression())
        val variable = VariableVisitor(environment).visit(ctx.variable())
        variable.setValue(evaluatedExpression)
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.variable().start) }
    }

    override fun visitCallStatement(ctx: RapiraLangParser.CallStatementContext) {
        val obj = ctx.IDENTIFIER()?.let {
            environment[it.text].getValue()
                .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = it.symbol) }
        }
            ?: expressionVisitor.visit(ctx.expression())

        val callable = obj as? RCallable
            ?: throw RapiraIllegalInvocationError(
                token = ctx.expression()?.start ?: ctx.IDENTIFIER().symbol
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

        ctx.repeatClause()?.let {
            val loopController = makeRepeatLoopController(it)
            allControllers.add(loopController)
        }

        ctx.forClause()?.let {
            val loopController = makeForLoopController(it)
            allControllers.add(loopController)
        }

        ctx.whileClause()?.let {
            val loopController = makeWhileLoopController(it)
            allControllers.add(loopController)
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

        ctx.variable()
            .forEach {
                val variable = variableVisitor.visit(it)
                variable.setValue(
                    if (isTextMode)
                        ConsoleReader.readText()
                    else
                        ConsoleReader.readObject()
                ).getOrThrow { reason -> RapiraInvalidOperationError(reason, token = it.start) }
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

    private fun makeRepeatLoopController(ctx: RapiraLangParser.RepeatClauseContext): LoopController {
        val expression = ctx.expression()

        val initialValue = expressionVisitor.visit(expression) as? RInteger
            ?: throw RapiraInvalidOperationError("Cannot call repeat with non-integer value", token = expression.start)

        if (initialValue.value < 0)
            throw RapiraInvalidOperationError("Cannot call repeat with negative integer value", token = expression.start)

        return RepeatLoopController(initialValue.value)
    }

    private fun makeForLoopController(ctx: RapiraLangParser.ForClauseContext): LoopController {
        val fromValue = ctx.fromExpr?.let { expressionVisitor.visit(it) }
        val toValue = ctx.toExpr?.let { expressionVisitor.visit(it) }
        val stepValue = ctx.stepExpr?.let { expressionVisitor.visit(it) }

        if (toValue != null && toValue !is RInteger && toValue !is Real) {
            throw RapiraInvalidOperationError("To value in for loop must be numeric", token = ctx.toExpr.start)
        }

        return ForLoopController(
            variable = environment[ctx.IDENTIFIER().text],
            fromValue = fromValue,
            toValue = toValue,
            stepValue = stepValue
        )
    }

    private fun makeWhileLoopController(ctx: RapiraLangParser.WhileClauseContext): LoopController =
        WhileLoopController(
            condition = ctx.expression(),
            expressionVisitor = expressionVisitor
        )
}
