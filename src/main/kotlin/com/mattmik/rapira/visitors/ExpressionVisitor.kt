package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.REmpty
import com.mattmik.rapira.objects.RFunction
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RLogical
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.RProcedure
import com.mattmik.rapira.objects.RReal
import com.mattmik.rapira.objects.RSequence
import com.mattmik.rapira.objects.RapiraCallable
import com.mattmik.rapira.objects.parseEscapedText

/**
 * A visitor that evaluates expressions while walking the tree within a given [Environment].
 *
 * @property environment the environment in which to evaluate expressions
 */
class ExpressionVisitor(private val environment: Environment) : RapiraLangBaseVisitor<RObject>() {

    override fun visitExpression(ctx: RapiraLangParser.ExpressionContext): RObject {
        return visit(ctx.logicalExpression())
    }

    override fun visitAndExpression(ctx: RapiraLangParser.AndExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.logicalExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return leftResult and rightResult
    }

    override fun visitOrExpression(ctx: RapiraLangParser.OrExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.logicalExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return leftResult or rightResult
    }

    override fun visitNotExpression(ctx: RapiraLangParser.NotExpressionContext): RObject {
        val result = visit(ctx.comparisonExpression())
        return result.not()
    }

    override fun visitRelationalExpression(ctx: RapiraLangParser.RelationalExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.comparisonExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
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
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return RLogical(
            if (ctx.op.type == RapiraLangParser.EQ)
                leftResult == rightResult
            else {
                leftResult != rightResult
            }
        )
    }

    override fun visitExponentiationExpression(ctx: RapiraLangParser.ExponentiationExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return leftResult.power(rightResult)
    }

    override fun visitMultiplicationExpression(ctx: RapiraLangParser.MultiplicationExpressionContext): RObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
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
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return when (ctx.op.type) {
            RapiraLangParser.PLUS -> leftResult + rightResult
            RapiraLangParser.MINUS -> leftResult - rightResult
            else -> super.visitAdditionExpression(ctx)
        }
    }

    override fun visitUnaryExpression(ctx: RapiraLangParser.UnaryExpressionContext): RObject {
        val result = this.visit(ctx.subopExpression())
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
                is RProcedure -> throw RapiraInvalidOperationError("Cannot invoke procedure within expression")
                is RapiraCallable -> return baseResult.call(environment, arguments) ?: REmpty
                else -> throw RapiraInvalidOperationError("Cannot invoke object that not a function")
            }
        }

        return REmpty
    }

    override fun visitLengthExpression(ctx: RapiraLangParser.LengthExpressionContext): RObject {
        val result = visit(ctx.subopExpression())
        return result.length()
    }

    override fun visitIdentifierValue(ctx: RapiraLangParser.IdentifierValueContext) =
        environment[ctx.IDENTIFIER().text]

    override fun visitIntValue(ctx: RapiraLangParser.IntValueContext) = RInteger(Integer.valueOf(ctx.text))

    override fun visitRealValue(ctx: RapiraLangParser.RealValueContext) = RReal(ctx.text.toDouble())

    override fun visitTextValue(ctx: RapiraLangParser.TextValueContext) = parseEscapedText(ctx.text)

    override fun visitProcedureDefinition(ctx: RapiraLangParser.ProcedureDefinitionContext): RObject {
        // TODO Add parsing of in-out params
        val params = ctx.procedureParams()?.IDENTIFIER()?.map { identifier -> identifier.text }
            ?: emptyList<String>()
        val extern = readExternIdentifiers(ctx.declarations())
        return RProcedure(ctx.stmts(), params, extern)
    }

    override fun visitFunctionDefinition(ctx: RapiraLangParser.FunctionDefinitionContext): RObject {
        val params = ctx.functionParams()?.IDENTIFIER()?.map { identifier -> identifier.text }
            ?: emptyList<String>()
        val extern = readExternIdentifiers(ctx.declarations())
        return RFunction(ctx.stmts(), params, extern)
    }

    override fun visitSequenceValue(ctx: RapiraLangParser.SequenceValueContext): RObject {
        val commaExpression = ctx.commaExpression()
        return if (commaExpression != null) visit(commaExpression) else RSequence()
    }

    override fun visitParentheticalExpression(
        ctx: RapiraLangParser.ParentheticalExpressionContext
    ): RObject = this.visit(ctx.expression())

    override fun visitCommaExpression(ctx: RapiraLangParser.CommaExpressionContext): RObject {
        val expressionResults = ctx.expression().map { visit(it) }
        return RSequence(expressionResults)
    }

    private fun readFunctionArguments(ctx: RapiraLangParser.FunctionArgumentsContext): List<Argument> =
        ctx.expression().map { expr -> InArgument(expr) }

    private fun readExternIdentifiers(ctx: RapiraLangParser.DeclarationsContext?) =
        ctx?.extern()?.IDENTIFIER()?.map { identifier -> identifier.text } ?: emptyList()
}
