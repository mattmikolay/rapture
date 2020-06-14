package com.mattmik.rapira.control

import com.mattmik.rapira.CONST_NO
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.visitors.ExpressionVisitor

class WhileLoopController(
    private val condition: RapiraParser.ExpressionContext,
    private val expressionVisitor: ExpressionVisitor
) : LoopController {

    override fun isLoopActive() =
        expressionVisitor.visit(condition) != CONST_NO

    override fun update() {
        // no-op
    }
}
