package com.mattmik.rapira.visitors

import com.mattmik.rapira.CONST_YES
import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraBaseVisitor
import com.mattmik.rapira.antlr.RapiraParser
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
import com.mattmik.rapira.errors.IllegalForLoopError
import com.mattmik.rapira.errors.IllegalInvocationError
import com.mattmik.rapira.errors.IllegalRepeatLoopError
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.RCallable
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.util.getOrThrow

/**
 * A visitor that executes statements while walking the tree within a given
 * [environment].
 */
class StatementVisitor(private val environment: Environment) : RapiraBaseVisitor<Unit>() {

    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitProcedureDefinition(ctx: RapiraParser.ProcedureDefinitionContext) {
        val procedure = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].setValue(procedure)
                .getOrThrow { reason -> InvalidOperationError(reason, token = it.symbol) }
        }
    }

    override fun visitFunctionDefinition(ctx: RapiraParser.FunctionDefinitionContext) {
        val function = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].setValue(function)
                .getOrThrow { reason -> InvalidOperationError(reason, token = it.symbol) }
        }
    }

    override fun visitAssignStatement(ctx: RapiraParser.AssignStatementContext) {
        val evaluatedExpression = expressionVisitor.visit(ctx.expression())
        val variable = VariableVisitor(environment).visit(ctx.variable())
        variable.setValue(evaluatedExpression)
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.variable().start) }
    }

    override fun visitCallStatement(ctx: RapiraParser.CallStatementContext) {
        val obj = ctx.IDENTIFIER()?.let {
            environment[it.text].getValue()
                .getOrThrow { reason -> InvalidOperationError(reason, token = it.symbol) }
        }
            ?: expressionVisitor.visit(ctx.expression())

        val callToken = ctx.expression()?.start ?: ctx.IDENTIFIER().symbol

        val callable = obj as? RCallable
            ?: throw IllegalInvocationError(token = callToken)

        val arguments = readProcedureArguments(ctx.procedureArguments())

        callable.call(environment, arguments, callToken)
    }

    override fun visitIfStatement(ctx: RapiraParser.IfStatementContext) {
        val conditionResult = expressionVisitor.visit(ctx.condition)
        if (conditionResult == Logical(true)) {
            visit(ctx.ifBody)
        } else ctx.elseBody?.let {
            visit(it)
        }
    }

    override fun visitConditionCaseStatement(ctx: RapiraParser.ConditionCaseStatementContext) {
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

    override fun visitConditionlessCaseStatement(ctx: RapiraParser.ConditionlessCaseStatementContext) {
        for (whenClause in ctx.singleWhenClause()) {
            val whenResult = expressionVisitor.visit(whenClause.expression())
            if (whenResult == CONST_YES) {
                visit(whenClause.stmts())
                return
            }
        }

        ctx.elseBody?.let { visit(it) }
    }

    override fun visitLoopStatement(ctx: RapiraParser.LoopStatementContext) {
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

            if (ctx.untilExpr?.let { expressionVisitor.visit(it) } == CONST_YES) {
                break
            }
        }
    }

    override fun visitOutputStatement(ctx: RapiraParser.OutputStatementContext) {
        val expressionResults = ctx.expression().map { expr -> expressionVisitor.visit(expr) }
        ConsoleWriter.printObjects(
            objects = expressionResults,
            lineBreak = ctx.nlf === null
        )
    }

    override fun visitInputStatement(ctx: RapiraParser.InputStatementContext) {
        val variableVisitor = VariableVisitor(environment)
        val isTextMode = ctx.inputMode?.type == RapiraParser.MODE_TEXT

        ctx.variable()
            .forEach {
                val variable = variableVisitor.visit(it)
                val obj = if (isTextMode) ConsoleReader.readText() else ConsoleReader.readObject()
                variable.setValue(obj ?: Empty)
                    .getOrThrow { reason -> InvalidOperationError(reason, token = it.start) }
        }
    }

    override fun visitExitStatement(ctx: RapiraParser.ExitStatementContext) =
        throw LoopExitException(token = ctx.LOOP_EXIT().symbol)

    override fun visitReturnStatement(ctx: RapiraParser.ReturnStatementContext) {
        val returnValue = ctx.expression()?.let {
            expressionVisitor.visit(it)
        }
        throw CallableReturnException(returnValue, token = ctx.RETURN().symbol)
    }

    // Expression statements are only valid in the REPL
    override fun visitExpressionStatement(ctx: RapiraParser.ExpressionStatementContext) {
        val expressionResult = expressionVisitor.visit(ctx.expression()) ?: Empty
        ConsoleWriter.println(expressionResult.toString())
    }

    private fun readProcedureArguments(ctx: RapiraParser.ProcedureArgumentsContext) =
        ctx.procedureArgument().map { arg ->
            arg.expression()?.let { expr -> InArgument(expr) }
                ?: InOutArgument(arg.variable())
        }

    private fun makeRepeatLoopController(ctx: RapiraParser.RepeatClauseContext): LoopController {
        val expression = ctx.expression()

        val initialValue = expressionVisitor.visit(expression)
        if (initialValue !is RInteger || initialValue.value < 0)
            throw IllegalRepeatLoopError(value = initialValue, token = expression.start)

        return RepeatLoopController(initialValue.value)
    }

    private fun makeForLoopController(ctx: RapiraParser.ForClauseContext): LoopController {
        val fromValue = ctx.fromExpr?.let { expressionVisitor.visit(it) }
        val toValue = ctx.toExpr?.let { expressionVisitor.visit(it) }
        val stepValue = ctx.stepExpr?.let { expressionVisitor.visit(it) }

        if (toValue != null && toValue !is RInteger && toValue !is Real) {
            throw IllegalForLoopError(value = toValue, token = ctx.toExpr.start)
        }

        return ForLoopController(
            variable = environment[ctx.IDENTIFIER().text],
            fromValue = fromValue,
            toValue = toValue,
            stepValue = stepValue,
            ctx = ctx
        )
    }

    private fun makeWhileLoopController(ctx: RapiraParser.WhileClauseContext): LoopController =
        WhileLoopController(
            condition = ctx.expression(),
            expressionVisitor = expressionVisitor
        )
}
