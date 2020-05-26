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
import com.mattmik.rapira.objects.ParamType
import com.mattmik.rapira.objects.Parameter
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RCallable
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.objects.parseEscapedText
import com.mattmik.rapira.objects.toLogical
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.getOrThrow
import com.mattmik.rapira.util.map

/**
 * A visitor that evaluates expressions while walking the tree within a given
 * [environment].
 */
class ExpressionVisitor(private val environment: Environment) : RapiraLangBaseVisitor<RObject>() {

    override fun visitAndExpression(ctx: RapiraLangParser.AndExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr and rightExpr }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.AND().symbol) }

    override fun visitOrExpression(ctx: RapiraLangParser.OrExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr or rightExpr }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.OR().symbol) }

    override fun visitNotExpression(ctx: RapiraLangParser.NotExpressionContext) =
        visit(ctx.expression())
            .not()
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.NOT().symbol) }

    override fun visitRelationalExpression(ctx: RapiraLangParser.RelationalExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr.compare(rightExpr) }
            .map { when (ctx.op.type) {
                RapiraLangParser.LESS -> it < 0
                RapiraLangParser.GREATER -> it > 0
                RapiraLangParser.LESSEQ -> it <= 0
                RapiraLangParser.GREATEREQ -> it >= 0
                else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
            } }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.op) }
            .toLogical()

    override fun visitEqualityExpression(ctx: RapiraLangParser.EqualityExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> if (ctx.op.type == RapiraLangParser.EQ) leftExpr == rightExpr else leftExpr != rightExpr }
            .toLogical()

    override fun visitExponentiationExpression(ctx: RapiraLangParser.ExponentiationExpressionContext) =
        ctx.arithmeticExpression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> leftExpr.power(rightExpr) }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.POWER().symbol) }

    override fun visitMultiplicationExpression(ctx: RapiraLangParser.MultiplicationExpressionContext) =
        ctx.arithmeticExpression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> when (ctx.op.type) {
                RapiraLangParser.MULT -> leftExpr * rightExpr
                RapiraLangParser.DIVIDE -> leftExpr / rightExpr
                RapiraLangParser.INTDIVIDE -> leftExpr.intDivide(rightExpr)
                RapiraLangParser.MOD -> leftExpr % rightExpr
                else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
            } }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.op) }

    override fun visitAdditionExpression(ctx: RapiraLangParser.AdditionExpressionContext) =
        ctx.arithmeticExpression()
            .map { visit(it) }
            .let { (leftExpr, rightExpr) -> when (ctx.op.type) {
                RapiraLangParser.PLUS -> leftExpr + rightExpr
                RapiraLangParser.MINUS -> leftExpr - rightExpr
                else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
            } }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.op) }

    override fun visitUnaryExpression(ctx: RapiraLangParser.UnaryExpressionContext) =
        visit(ctx.subopExpression())
            .let { if (ctx.op?.type == RapiraLangParser.MINUS) it.negate() else Result.Success(it) }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.op) }

    override fun visitIndexCommaExpression(ctx: RapiraLangParser.IndexCommaExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())

        return ctx.commaExpression().expression()
            .map { visit(it) }
            .fold(baseResult) { result, index ->
                result.elementAt(index)
                    .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
            }
    }

    override fun visitIndexColonExpression(ctx: RapiraLangParser.IndexColonExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())

        val leftExpr = ctx.leftExpr?.let { expr -> visit(expr) }
        val rightExpr = ctx.rightExpr?.let { expr -> visit(expr) }

        if (leftExpr == null && rightExpr == null) {
            return baseResult
        }

        return baseResult.slice(start = leftExpr, end = rightExpr)
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.COLON().symbol) }
    }

    override fun visitFunctionInvocationExpression(ctx: RapiraLangParser.FunctionInvocationExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())
        val arguments = readFunctionArguments(ctx.functionArguments())

        val leftParenToken = ctx.functionArguments().LPAREN().symbol

        return when (baseResult) {
            is Procedure ->
                throw RapiraInvalidOperationError("Cannot invoke procedure within expression", token = leftParenToken)
            is RCallable ->
                baseResult.call(environment, arguments) ?: Empty
            else ->
                throw RapiraIllegalInvocationError(token = leftParenToken)
        }
    }

    override fun visitLengthExpression(ctx: RapiraLangParser.LengthExpressionContext) =
        visit(ctx.subopExpression())
            .length()
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.HASH().symbol) }

    override fun visitIdentifierValue(ctx: RapiraLangParser.IdentifierValueContext) =
        environment[ctx.IDENTIFIER().text]
            .getValue()
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.IDENTIFIER().symbol) }

    override fun visitIntValue(ctx: RapiraLangParser.IntValueContext) =
        Integer.valueOf(ctx.text).toRInteger()

    override fun visitRealValue(ctx: RapiraLangParser.RealValueContext) =
        Real(ctx.text.toDouble())

    override fun visitTextValue(ctx: RapiraLangParser.TextValueContext) =
        parseEscapedText(ctx.text)

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

    override fun visitSequenceValue(ctx: RapiraLangParser.SequenceValueContext) =
        (ctx.commaExpression()?.expression() ?: emptyList())
            .map { visit(it) }
            .toSequence()

    override fun visitParentheticalExpression(ctx: RapiraLangParser.ParentheticalExpressionContext): RObject =
        visit(ctx.expression())

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
