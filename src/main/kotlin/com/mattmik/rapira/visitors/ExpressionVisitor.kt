package com.mattmik.rapira.visitors

import com.mattmik.rapira.antlr.RapiraLangBaseVisitor
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.RapiraInteger
import com.mattmik.rapira.objects.RapiraObject

class ExpressionVisitor : RapiraLangBaseVisitor<RapiraObject>() {

    override fun visitExpression(ctx: RapiraLangParser.ExpressionContext): RapiraObject {
        val result = this.visit(ctx.logicalExpression())

        // TODO ExpressionVisitor should not print to console
        println(result)

        return result
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

    override fun visitIntValue(ctx: RapiraLangParser.IntValueContext) = RapiraInteger(Integer.valueOf(ctx.text))
}
