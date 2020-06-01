package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraBaseVisitor
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.errors.IllegalInvocationError
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RCallable
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.objects.parseEscapedText
import com.mattmik.rapira.objects.toLogical
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.getOrThrow
import com.mattmik.rapira.util.map

/**
 * A visitor that evaluates expressions while walking the tree within a given
 * [environment].
 */
class ExpressionVisitor(private val environment: Environment) : RapiraBaseVisitor<RObject>() {

    override fun visitAndExpression(ctx: RapiraParser.AndExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr and rightExpr }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.AND().symbol) }

    override fun visitOrExpression(ctx: RapiraParser.OrExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr or rightExpr }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.OR().symbol) }

    override fun visitNotExpression(ctx: RapiraParser.NotExpressionContext) =
        visit(ctx.expression())
            .not()
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.NOT().symbol) }

    override fun visitRelationalExpression(ctx: RapiraParser.RelationalExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr.compare(rightExpr) }
            .map { when (ctx.op.type) {
                RapiraParser.LESS -> it < 0
                RapiraParser.GREATER -> it > 0
                RapiraParser.LESSEQ -> it <= 0
                RapiraParser.GREATEREQ -> it >= 0
                else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
            } }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.op) }
            .toLogical()

    override fun visitEqualityExpression(ctx: RapiraParser.EqualityExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> if (ctx.op.type == RapiraParser.EQ) leftExpr == rightExpr else leftExpr != rightExpr }
            .toLogical()

    override fun visitExponentiationExpression(ctx: RapiraParser.ExponentiationExpressionContext) =
        ctx.arithmeticExpression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr.power(rightExpr) }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.POWER().symbol) }

    override fun visitMultiplicationExpression(ctx: RapiraParser.MultiplicationExpressionContext) =
        ctx.arithmeticExpression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> when (ctx.op.type) {
                RapiraParser.MULT -> leftExpr * rightExpr
                RapiraParser.DIVIDE -> leftExpr / rightExpr
                RapiraParser.INTDIVIDE -> leftExpr.intDivide(rightExpr)
                RapiraParser.MOD -> leftExpr % rightExpr
                else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
            } }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.op) }

    override fun visitAdditionExpression(ctx: RapiraParser.AdditionExpressionContext) =
        ctx.arithmeticExpression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> when (ctx.op.type) {
                RapiraParser.PLUS -> leftExpr + rightExpr
                RapiraParser.MINUS -> leftExpr - rightExpr
                else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
            } }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.op) }

    override fun visitUnaryExpression(ctx: RapiraParser.UnaryExpressionContext) =
        visit(ctx.subopExpression())
            .let { if (ctx.op?.type == RapiraParser.MINUS) it.negate() else Result.Success(it) }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.op) }

    override fun visitIndexCommaExpression(ctx: RapiraParser.IndexCommaExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())

        return ctx.commaExpression().expression()
            .map { visit(it) }
            .fold(baseResult) { result, index ->
                result.elementAt(index)
                    .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.LBRACKET().symbol) }
            }
    }

    override fun visitIndexColonExpression(ctx: RapiraParser.IndexColonExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())

        val leftExpr = ctx.leftExpr?.let { expr -> visit(expr) }
        val rightExpr = ctx.rightExpr?.let { expr -> visit(expr) }

        return baseResult.slice(start = leftExpr, end = rightExpr)
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.COLON().symbol) }
    }

    override fun visitFunctionInvocationExpression(ctx: RapiraParser.FunctionInvocationExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())
        val arguments = readFunctionArguments(ctx.functionArguments())

        val leftParenToken = ctx.functionArguments().LPAREN().symbol

        return when (baseResult) {
            is Procedure ->
                throw InvalidOperationError("Cannot invoke procedure within expression", token = leftParenToken)
            is RCallable ->
                baseResult.call(environment, arguments, callToken = leftParenToken) ?: Empty
            else ->
                throw IllegalInvocationError(token = leftParenToken)
        }
    }

    override fun visitLengthExpression(ctx: RapiraParser.LengthExpressionContext) =
        visit(ctx.subopExpression())
            .length()
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.HASH().symbol) }

    override fun visitIdentifierValue(ctx: RapiraParser.IdentifierValueContext) =
        environment[ctx.IDENTIFIER().text]
            .getValue()
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.IDENTIFIER().symbol) }

    override fun visitIntValue(ctx: RapiraParser.IntValueContext) =
        Integer.valueOf(ctx.text).toRInteger()

    override fun visitRealValue(ctx: RapiraParser.RealValueContext) =
        Real(ctx.text.toDouble())

    override fun visitTextValue(ctx: RapiraParser.TextValueContext) =
        parseEscapedText(ctx.text)

    override fun visitProcedureDefinition(ctx: RapiraParser.ProcedureDefinitionContext): RObject {
        val procedureName = ctx.IDENTIFIER()?.text
        val params = readProcedureParams(ctx.procedureParams())
        val extern = readExternIdentifiers(ctx.declarations())
        return Procedure(procedureName, ctx.stmts(), params, extern)
    }

    override fun visitFunctionDefinition(ctx: RapiraParser.FunctionDefinitionContext): RObject {
        val functionName = ctx.IDENTIFIER()?.text
        val params = readFunctionParams(ctx.functionParams())
        val extern = readExternIdentifiers(ctx.declarations())
        return Function(functionName, ctx.stmts(), params, extern)
    }

    override fun visitSequenceValue(ctx: RapiraParser.SequenceValueContext) =
        (ctx.commaExpression()?.expression() ?: emptyList())
            .map { visit(it) }
            .toSequence()

    override fun visitParentheticalExpression(ctx: RapiraParser.ParentheticalExpressionContext): RObject =
        visit(ctx.expression())

    private fun readProcedureParams(ctx: RapiraParser.ProcedureParamsContext?): List<Parameter> =
        (ctx?.procedureParam() ?: emptyList())
            .map { paramContext ->
                paramContext.inParam()?.let { readInParam(it) }
                    ?: readInOutParam(paramContext.inOutParam())
            }

    private fun readFunctionParams(ctx: RapiraParser.FunctionParamsContext?): List<Parameter> =
        (ctx?.inParam() ?: emptyList())
            .map { readInParam(it) }

    private fun readInParam(ctx: RapiraParser.InParamContext): Parameter {
        val paramName = ctx.IDENTIFIER().text

        if (Environment.isReserved(paramName)) {
            throw InvalidOperationError(
                "Param name $paramName is reserved word",
                token = ctx.IDENTIFIER().symbol
            )
        }

        return Parameter(type = ParamType.In, name = paramName)
    }

    private fun readInOutParam(ctx: RapiraParser.InOutParamContext): Parameter {
        val paramName = ctx.IDENTIFIER().text

        if (Environment.isReserved(paramName)) {
            throw InvalidOperationError(
                "Param name $paramName is reserved word",
                token = ctx.IDENTIFIER().symbol
            )
        }

        return Parameter(type = ParamType.InOut, name = paramName)
    }

    private fun readFunctionArguments(ctx: RapiraParser.FunctionArgumentsContext): List<Argument> =
        ctx.expression().map { expr -> InArgument(expr) }

    private fun readExternIdentifiers(ctx: RapiraParser.DeclarationsContext?) =
        ctx?.extern()?.IDENTIFIER()?.map { identifier -> identifier.text } ?: emptyList()
}
