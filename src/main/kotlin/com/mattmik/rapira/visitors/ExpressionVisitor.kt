package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.ParamType
import com.mattmik.rapira.objects.Parameter
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.RapiraCallable
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.parseEscapedText

/**
 * A visitor that evaluates expressions while walking the tree within a given [Environment].
 *
 * @property environment the environment in which to evaluate expressions
 */
class ExpressionVisitor(private val environment: Environment) : RapiraLangBaseVisitor<RObject>() {

    override fun visitAndExpression(ctx: RapiraLangParser.AndExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.logicalExpression()
        val leftResult = visit(leftExpression)
        val rightResult = visit(rightExpression)
        return leftResult and rightResult
    }

    override fun visitOrExpression(ctx: RapiraLangParser.OrExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.logicalExpression()
        val leftResult = visit(leftExpression)
        val rightResult = visit(rightExpression)
        return leftResult or rightResult
    }

    override fun visitNotExpression(ctx: RapiraLangParser.NotExpressionContext): RObject {
        val result = visit(ctx.comparisonExpression())
        return result.not()
    }

    override fun visitRelationalExpression(ctx: RapiraLangParser.RelationalExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.comparisonExpression()
        val leftResult = visit(leftExpression)
        val rightResult = visit(rightExpression)
        return when (ctx.op.type) {
            RapiraLangParser.LESS -> leftResult lessThan rightResult
            RapiraLangParser.GREATER -> leftResult greaterThan rightResult
            RapiraLangParser.LESSEQ -> leftResult lessThanEqualTo rightResult
            RapiraLangParser.GREATEREQ -> leftResult greaterThanEqualTo rightResult
            else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
        }
    }

    override fun visitEqualityExpression(ctx: RapiraLangParser.EqualityExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.comparisonExpression()
        val leftResult = visit(leftExpression)
        val rightResult = visit(rightExpression)
        return Logical(
            if (ctx.op.type == RapiraLangParser.EQ)
                leftResult == rightResult
            else {
                leftResult != rightResult
            }
        )
    }

    override fun visitExponentiationExpression(ctx: RapiraLangParser.ExponentiationExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = visit(leftExpression)
        val rightResult = visit(rightExpression)
        return leftResult.power(rightResult)
    }

    override fun visitMultiplicationExpression(ctx: RapiraLangParser.MultiplicationExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = visit(leftExpression)
        val rightResult = visit(rightExpression)
        return when (ctx.op.type) {
            RapiraLangParser.MULT -> leftResult * rightResult
            RapiraLangParser.DIVIDE -> leftResult / rightResult
            RapiraLangParser.INTDIVIDE -> leftResult.intDivide(rightResult)
            RapiraLangParser.MOD -> leftResult % rightResult
            else -> super.visitMultiplicationExpression(ctx)
        }
    }

    override fun visitAdditionExpression(ctx: RapiraLangParser.AdditionExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = visit(leftExpression)
        val rightResult = visit(rightExpression)
        return when (ctx.op.type) {
            RapiraLangParser.PLUS -> leftResult + rightResult
            RapiraLangParser.MINUS -> leftResult - rightResult
            else -> super.visitAdditionExpression(ctx)
        }
    }

    override fun visitUnaryExpression(ctx: RapiraLangParser.UnaryExpressionContext): RObject {
        val result = visit(ctx.subopExpression())
        return if (ctx.op?.type == RapiraLangParser.MINUS) result.negate() else result
    }

    override fun visitSubopModifiedExpression(ctx: RapiraLangParser.SubopModifiedExpressionContext): RObject {
        val baseResult = visit(ctx.subopExpression())

        ctx.indexExpression()?.let {
            val evaluatedCommaExpressions = it.commaExpression()?.expression()?.map { expr -> visit(expr) }
            if (evaluatedCommaExpressions != null) {
                return evaluatedCommaExpressions.fold(baseResult) { result, index ->
                    result.elementAt(index)
                }
            }

            val leftOfColon = it.leftIndex?.let { expr -> visit(expr) }
            val rightOfColon = it.rightIndex?.let { expr -> visit(expr) }
            return baseResult.slice(leftOfColon, rightOfColon)
        }

        ctx.functionArguments()?.let {
            val arguments = readFunctionArguments(it)

            when (baseResult) {
                is Procedure -> throw RapiraInvalidOperationError("Cannot invoke procedure within expression")
                is RapiraCallable -> return baseResult.call(environment, arguments) ?: Empty
                else -> throw RapiraInvalidOperationError("Cannot invoke object that not a function")
            }
        }

        return Empty
    }

    override fun visitLengthExpression(ctx: RapiraLangParser.LengthExpressionContext): RObject {
        val result = visit(ctx.subopExpression())
        return result.length()
    }

    override fun visitIdentifierValue(ctx: RapiraLangParser.IdentifierValueContext) =
        environment[ctx.IDENTIFIER().text].value

    override fun visitIntValue(ctx: RapiraLangParser.IntValueContext) = RInteger(Integer.valueOf(ctx.text))

    override fun visitRealValue(ctx: RapiraLangParser.RealValueContext) = Real(ctx.text.toDouble())

    override fun visitTextValue(ctx: RapiraLangParser.TextValueContext) = parseEscapedText(ctx.text)

    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext): RObject {
        val params = readProcedureParams(ctx.procedureParams())
        val extern = readExternIdentifiers(ctx.declarations())
        return Procedure(ctx.stmts(), params, extern)
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext): RObject {
        val params = readFunctionParams(ctx.functionParams())
        val extern = readExternIdentifiers(ctx.declarations())
        return Function(ctx.stmts(), params, extern)
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

    private fun readProcedureParams(ctx: RapiraLangParser.ProcedureParamsContext): List<Parameter> =
        ctx.procedureParam()?.map { paramContext ->
            paramContext.inParam()?.let { Parameter(ParamType.In, it.IDENTIFIER().text) }
                ?: paramContext.inOutParam()?.let { Parameter(ParamType.InOut, it.IDENTIFIER().text) }
                ?: throw RapiraInvalidOperationError("Invalid param type")
        } ?: emptyList()

    private fun readFunctionParams(ctx: RapiraLangParser.FunctionParamsContext): List<Parameter> =
        ctx.inParam()?.map { paramContext -> Parameter(ParamType.In, paramContext.IDENTIFIER().text) }
            ?: emptyList()

    private fun readFunctionArguments(ctx: RapiraLangParser.FunctionArgumentsContext): List<Argument> =
        ctx.expression().map { expr -> InArgument(expr) }

    private fun readExternIdentifiers(ctx: RapiraLangParser.DeclarationsContext?) =
        ctx?.extern()?.IDENTIFIER()?.map { identifier -> identifier.text } ?: emptyList()
}
