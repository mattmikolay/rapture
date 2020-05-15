package com.mattmik.rapira.visitors

import com.mattmik.rapira.ConsoleReader
import com.mattmik.rapira.ConsoleWriter
import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.errors.RapiraIllegalInvocationError
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.LogicalNo
import com.mattmik.rapira.objects.LogicalYes
import com.mattmik.rapira.objects.RCallable
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Real

/**
 * A visitor that executes statements while walking the tree within a given [environment].
 */
class StatementVisitor(private val environment: Environment) : RapiraLangBaseVisitor<Unit>() {

    private val expressionVisitor = ExpressionVisitor(environment)

    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext) {
        val procedure = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].value = procedure
        }
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext) {
        val function = expressionVisitor.visit(ctx)
        ctx.IDENTIFIER()?.let {
            environment[it.text].value = function
        }
    }

    override fun visitAssignStatement(ctx: RapiraLangParser.AssignStatementContext) {
        val evaluatedExpression = expressionVisitor.visit(ctx.expression())
        val variable = VariableVisitor(environment).visit(ctx.variable())
        variable.value = evaluatedExpression
    }

    override fun visitCallStatement(ctx: RapiraLangParser.CallStatementContext) {
        val callable = ctx.IDENTIFIER()?.let { environment[it.text].value }
            ?: expressionVisitor.visit(ctx.expression())

        val arguments = readProcedureArguments(ctx.procedureArguments())

        (callable as? RCallable)?.call(environment, arguments)
            ?: throw RapiraIllegalInvocationError(token = ctx.CALL()?.symbol ?: ctx.IDENTIFIER().symbol)
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
        var repeatCounter = ctx.repeatClause()?.expression()?.let {
            val repeatExprResult = expressionVisitor.visit(it)
            if (repeatExprResult !is RInteger || repeatExprResult.value < 0) {
                throw RapiraInvalidOperationError("Cannot call repeat with non-integer number")
            }
            repeatExprResult.value
        }

        val forIdentifier = ctx.forClause()?.IDENTIFIER()?.text
        if (forIdentifier != null) {
            // Set initial value using "from" expression
            environment[forIdentifier].value =
                ctx.forClause().fromExpr?.let { expressionVisitor.visit(it) } ?: RInteger(1)
        }

        val toValue = ctx.forClause()?.toExpr?.let { expressionVisitor.visit(it) }
        if (toValue != null && toValue !is RInteger && toValue !is Real) {
            throw RapiraInvalidOperationError("To value in for loop must be number")
        }

        val stepValue = ctx.forClause()?.stepExpr?.let { expressionVisitor.visit(it) } ?: RInteger(1)

        while (
            (repeatCounter == null || repeatCounter > 0) &&
            ctx.whileClause()?.expression()?.let { expressionVisitor.visit(it) } != LogicalNo &&
            (forIdentifier == null || toValue == null || (toValue - environment[forIdentifier].value) * stepValue >= RInteger(
                0
            ))
        ) {

            try {
                visit(ctx.stmts())
            } catch (exception: LoopExitException) {
                break
            }

            // Update forIdentifier using "step" expression
            if (forIdentifier != null) {
                environment[forIdentifier].value = environment[forIdentifier].value + stepValue
            }

            if (repeatCounter != null) {
                repeatCounter--
            }

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
            variable.value = if (isTextMode) ConsoleReader.readText() else ConsoleReader.readObject()
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
