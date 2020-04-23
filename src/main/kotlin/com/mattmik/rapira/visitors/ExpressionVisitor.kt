package com.mattmik.rapira.visitors

import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.*

class ExpressionVisitor : RapiraLangBaseVisitor<RapiraObject>() {

    override fun visitExpression(ctx: RapiraLangParser.ExpressionContext): RapiraObject {
        return visit(ctx.logicalExpression())
    }

    override fun visitAndExpression(ctx: RapiraLangParser.AndExpressionContext): RapiraObject {
        val (leftExpression, rightExpression) = ctx.logicalExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return leftResult.and(rightResult)
    }

    override fun visitOrExpression(ctx: RapiraLangParser.OrExpressionContext): RapiraObject {
        val (leftExpression, rightExpression) = ctx.logicalExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return leftResult.or(rightResult)
    }

    override fun visitNotExpression(ctx: RapiraLangParser.NotExpressionContext): RapiraObject {
        val result = visit(ctx.comparisonExpression())
        return result.not()
    }

    override fun visitRelationalExpression(ctx: RapiraLangParser.RelationalExpressionContext): RapiraObject {
        val (leftExpression, rightExpression) = ctx.comparisonExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return when (ctx.op.type) {
            RapiraLangParser.LESS -> leftResult.lessThan(rightResult)
            RapiraLangParser.GREATER -> leftResult.greaterThan(rightResult)
            RapiraLangParser.LESSEQ -> leftResult.lessThanEqualTo(rightResult)
            RapiraLangParser.GREATEREQ -> leftResult.greaterThanEqualTo(rightResult)
            else -> throw IllegalStateException("Fatal: encountered unexpected token of type ${ctx.op.type}")
        }
    }

    override fun visitEqualityExpression(ctx: RapiraLangParser.EqualityExpressionContext): RapiraObject {
        val (leftExpression, rightExpression) = ctx.comparisonExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return RapiraLogical(
            if (ctx.op.type == RapiraLangParser.EQ)
                leftResult == rightResult
            else {
                leftResult != rightResult
            }
        )
    }

    override fun visitExponentiationExpression(ctx: RapiraLangParser.ExponentiationExpressionContext): RapiraObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return leftResult.power(rightResult)
    }

    override fun visitMultiplicationExpression(ctx: RapiraLangParser.MultiplicationExpressionContext): RapiraObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return when (ctx.op.type) {
            RapiraLangParser.MULT -> leftResult.multiply(rightResult)
            RapiraLangParser.DIVIDE -> leftResult.divide(rightResult)
            RapiraLangParser.INTDIVIDE -> leftResult.intDivide(rightResult)
            RapiraLangParser.MOD -> leftResult.modulus(rightResult)
            else -> super.visitMultiplicationExpression(ctx)
        }
    }

    override fun visitAdditionExpression(ctx: RapiraLangParser.AdditionExpressionContext): RapiraObject {
        val (leftExpression, rightExpression) = ctx.arithmeticExpression()
        val leftResult = this.visit(leftExpression)
        val rightResult = this.visit(rightExpression)
        return when (ctx.op.type) {
            RapiraLangParser.PLUS -> leftResult.add(rightResult)
            RapiraLangParser.MINUS -> leftResult.subtract(rightResult)
            else -> super.visitAdditionExpression(ctx)
        }
    }

    override fun visitUnaryExpression(ctx: RapiraLangParser.UnaryExpressionContext): RapiraObject {
        val result = this.visit(ctx.subopExpression())
        return if (ctx.op?.type == RapiraLangParser.MINUS) result.negate() else result
    }

    override fun visitIntValue(ctx: RapiraLangParser.IntValueContext) = RapiraInteger(Integer.valueOf(ctx.text))

    override fun visitRealValue(ctx: RapiraLangParser.RealValueContext) = RapiraReal(ctx.text.toDouble())

    override fun visitTextValue(ctx: RapiraLangParser.TextValueContext) = parseEscapedText(ctx.text)

    override fun visitSequenceValue(ctx: RapiraLangParser.SequenceValueContext): RapiraObject {
        val commaExpression = ctx.commaExpression()
        return if (commaExpression != null) visit(commaExpression) else RapiraSequence()
    }

    override fun visitParentheticalExpression(
        ctx: RapiraLangParser.ParentheticalExpressionContext
    ): RapiraObject = this.visit(ctx.expression())

    override fun visitCommaExpression(ctx: RapiraLangParser.CommaExpressionContext): RapiraObject {
        val expressionResults = ctx.expression().map { visit(it) }
        return RapiraSequence(expressionResults)
    }
}
