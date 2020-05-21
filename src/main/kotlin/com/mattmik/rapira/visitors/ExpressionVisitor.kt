package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.errors.RapiraIllegalInvocationError
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.OperationResult
import com.mattmik.rapira.objects.ParamType
import com.mattmik.rapira.objects.Parameter
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RCallable
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.parseEscapedText

/**
 * A visitor that evaluates expressions while walking the tree within a given [environment].
 */
class ExpressionVisitor(private val environment: Environment) : RapiraLangBaseVisitor<RObject>() {

    override fun visitAndExpression(ctx: RapiraLangParser.AndExpressionContext): RObject {
        val (leftExpr, rightExpr) = ctx.expression().map { visit(it) }
        return when (val operationResult = leftExpr and rightExpr) {
            is OperationResult.Success -> operationResult.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason, token = ctx.AND().symbol)
        }
    }

    override fun visitOrExpression(ctx: RapiraLangParser.OrExpressionContext): RObject {
        val (leftExpr, rightExpr) = ctx.expression().map { visit(it) }
        return when (val operationResult = leftExpr or rightExpr) {
            is OperationResult.Success -> operationResult.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason, token = ctx.OR().symbol)
        }
    }

    override fun visitNotExpression(ctx: RapiraLangParser.NotExpressionContext): RObject {
        val result = visit(ctx.expression())
        return when (val operationResult = result.not()) {
            is OperationResult.Success -> operationResult.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason, token = ctx.NOT().symbol)
        }
    }

    override fun visitRelationalExpression(ctx: RapiraLangParser.RelationalExpressionContext): RObject {
        val (leftExpr, rightExpr) = ctx.expression()
        val leftResult = visit(leftExpr)
        val rightResult = visit(rightExpr)
        return when (ctx.op.type) {
            RapiraLangParser.LESS -> Logical(leftResult < rightResult)
            RapiraLangParser.GREATER -> Logical(leftResult > rightResult)
            RapiraLangParser.LESSEQ -> Logical(leftResult <= rightResult)
            RapiraLangParser.GREATEREQ -> Logical(leftResult >= rightResult)
            else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
        }
    }

    override fun visitEqualityExpression(ctx: RapiraLangParser.EqualityExpressionContext): RObject {
        val (leftExpr, rightExpr) = ctx.expression()
        val leftResult = visit(leftExpr)
        val rightResult = visit(rightExpr)
        return Logical(
            if (ctx.op.type == RapiraLangParser.EQ)
                leftResult == rightResult
            else {
                leftResult != rightResult
            }
        )
    }

    override fun visitExponentiationExpression(ctx: RapiraLangParser.ExponentiationExpressionContext): RObject {
        val (leftExpr, rightExpr) = ctx.arithmeticExpression().map { visit(it) }
        return when (val operationResult = leftExpr.power(rightExpr)) {
            is OperationResult.Success -> operationResult.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason, token = ctx.POWER().symbol)
        }
    }

    override fun visitMultiplicationExpression(ctx: RapiraLangParser.MultiplicationExpressionContext): RObject {
        val (leftExpr, rightExpr) = ctx.arithmeticExpression().map { visit(it) }

        val operationResult = when (ctx.op.type) {
            RapiraLangParser.MULT -> leftExpr * rightExpr
            RapiraLangParser.DIVIDE -> leftExpr / rightExpr
            RapiraLangParser.INTDIVIDE -> leftExpr.intDivide(rightExpr)
            RapiraLangParser.MOD -> leftExpr % rightExpr
            else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
        }

        return when (operationResult) {
            is OperationResult.Success -> operationResult.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason, token = ctx.op)
        }
    }

    override fun visitAdditionExpression(ctx: RapiraLangParser.AdditionExpressionContext): RObject {
        val (leftExpr, rightExpr) = ctx.arithmeticExpression()
        val leftResult = visit(leftExpr)
        val rightResult = visit(rightExpr)
        return when (ctx.op.type) {
            RapiraLangParser.PLUS -> leftResult + rightResult
            RapiraLangParser.MINUS -> leftResult - rightResult
            else -> super.visitAdditionExpression(ctx)
        }
    }

    override fun visitUnaryExpression(ctx: RapiraLangParser.UnaryExpressionContext): RObject {
        val result = visit(ctx.subopExpression())
        if (ctx.op?.type != RapiraLangParser.MINUS)
            return result

        return when (val operationResult = result.negate()) {
            is OperationResult.Success -> operationResult.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason, token = ctx.op)
        }
    }

    override fun visitSubopModifiedExpression(ctx: RapiraLangParser.SubopModifiedExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())

        ctx.indexExpression()?.let {
            val evaluatedCommaExpressions = it.commaExpression()?.expression()?.map { expr -> visit(expr) }
            if (evaluatedCommaExpressions != null) {
                return evaluatedCommaExpressions.fold(baseResult) { result, index ->
                    when (val operationResult = result.elementAt(index)) {
                        is OperationResult.Success -> operationResult.obj
                        is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason)
                    }
                }
            }

            val leftOfColon = it.leftIndex?.let { expr -> visit(expr) }
            val rightOfColon = it.rightIndex?.let { expr -> visit(expr) }
            return baseResult.slice(leftOfColon, rightOfColon)
        }

        ctx.functionArguments()?.let {
            val arguments = readFunctionArguments(it)

            return when (baseResult) {
                is Procedure -> throw RapiraInvalidOperationError("Cannot invoke procedure within expression")
                is RCallable -> baseResult.call(environment, arguments) ?: Empty
                else -> throw RapiraIllegalInvocationError(it.LPAREN().symbol)
            }
        }

        return Empty
    }

    override fun visitLengthExpression(ctx: RapiraLangParser.LengthExpressionContext): RObject {
        val result = visit(ctx.subopExpression())
        return when (val operationResult = result.length()) {
            is OperationResult.Success -> operationResult.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(operationResult.reason)
        }
    }

    override fun visitIdentifierValue(ctx: RapiraLangParser.IdentifierValueContext) =
        environment[ctx.IDENTIFIER().text].value

    override fun visitIntValue(ctx: RapiraLangParser.IntValueContext) = RInteger(Integer.valueOf(ctx.text))

    override fun visitRealValue(ctx: RapiraLangParser.RealValueContext) = Real(ctx.text.toDouble())

    override fun visitTextValue(ctx: RapiraLangParser.TextValueContext) = parseEscapedText(ctx.text)

    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext): RObject {
        val procedureName = ctx.IDENTIFIER()?.text
        val params = readProcedureParams(ctx.procedureParams())
        val extern = readExternIdentifiers(ctx.declarations())
        return Procedure(procedureName, ctx.stmts(), params, extern)
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext): RObject {
        val functionName = ctx.IDENTIFIER()?.text
        val params = readFunctionParams(ctx.functionParams())
        val extern = readExternIdentifiers(ctx.declarations())
        return Function(functionName, ctx.stmts(), params, extern)
    }

    override fun visitSequenceValue(ctx: RapiraLangParser.SequenceValueContext): RObject {
        val commaExpression = ctx.commaExpression()
        return if (commaExpression != null) visit(commaExpression) else Sequence()
    }

    override fun visitParentheticalExpression(
        ctx: RapiraLangParser.ParentheticalExpressionContext
    ): RObject = visit(ctx.expression())

    override fun visitCommaExpression(ctx: RapiraLangParser.CommaExpressionContext): RObject {
        val expressionResults = ctx.expression().map { visit(it) }
        return Sequence(expressionResults)
    }

    private fun readProcedureParams(ctx: RapiraLangParser.ProcedureParamsContext?): List<Parameter> =
        ctx?.procedureParam()?.map { paramContext ->
            paramContext.inParam()?.let { Parameter(ParamType.In, it.IDENTIFIER().text) }
                ?: Parameter(ParamType.InOut, paramContext.inOutParam().IDENTIFIER().text)
        } ?: emptyList()

    private fun readFunctionParams(ctx: RapiraLangParser.FunctionParamsContext?): List<Parameter> =
        ctx?.inParam()?.map { paramContext -> Parameter(ParamType.In, paramContext.IDENTIFIER().text) }
            ?: emptyList()

    private fun readFunctionArguments(ctx: RapiraLangParser.FunctionArgumentsContext): List<Argument> =
        ctx.expression().map { expr -> InArgument(expr) }

    private fun readExternIdentifiers(ctx: RapiraLangParser.DeclarationsContext?) =
        ctx?.extern()?.IDENTIFIER()?.map { identifier -> identifier.text } ?: emptyList()
}
