package com.mattmik.rapira.control

import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.LogicalNo
import com.mattmik.rapira.visitors.ExpressionVisitor

class WhileLoopController(
    private val condition: RapiraLangParser.ExpressionContext,
    private val expressionVisitor: ExpressionVisitor
) : LoopController {

    override fun isLoopActive() =
        expressionVisitor.visit(condition) != LogicalNo

    override fun update() {
        // no-op
    }
}
