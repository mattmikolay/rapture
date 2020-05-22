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
import com.mattmik.rapira.objects.OperationResult
import com.mattmik.rapira.objects.ParamType
import com.mattmik.rapira.objects.Parameter
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RCallable
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.getOrThrow
import com.mattmik.rapira.objects.parseEscapedText
import com.mattmik.rapira.objects.toLogical
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toSequence

/**
 * A visitor that evaluates expressions while walking the tree within a given [environment].
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
            .let { (leftExpr, rightExpr) -> when (ctx.op.type) {
                RapiraLangParser.LESS -> leftExpr < rightExpr
                RapiraLangParser.GREATER -> leftExpr > rightExpr
                RapiraLangParser.LESSEQ -> leftExpr <= rightExpr
                RapiraLangParser.GREATEREQ -> leftExpr >= rightExpr
                else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
            } }
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
            .let { if (ctx.op?.type == RapiraLangParser.MINUS) it.negate() else OperationResult.Success(it) }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.op) }

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
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
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

    override fun visitLengthExpression(ctx: RapiraLangParser.LengthExpressionContext) =
        visit(ctx.subopExpression())
            .length()
            .getOrThrow { reason -> RapiraInvalidOperationError(reason, token = ctx.HASH().symbol) }

    override fun visitIdentifierValue(ctx: RapiraLangParser.IdentifierValueContext) =
        environment[ctx.IDENTIFIER().text].value

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

    override fun visitSequenceValue(ctx: RapiraLangParser.SequenceValueContext): RObject {
        val commaExpression = ctx.commaExpression()
        return if (commaExpression != null) visit(commaExpression) else Sequence()
    }

    override fun visitParentheticalExpression(
        ctx: RapiraLangParser.ParentheticalExpressionContext
    ): RObject = visit(ctx.expression())

    override fun visitCommaExpression(ctx: RapiraLangParser.CommaExpressionContext) =
        ctx.expression()
            .map { visit(it) }
            .toSequence()

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
