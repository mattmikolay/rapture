package com.mattmik.rapira.control

import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.util.andThen
import com.mattmik.rapira.util.getOrThrow
import com.mattmik.rapira.variables.Variable

class ForLoopController(
    private val variable: Variable,
    fromValue: RObject?,
    private val toValue: RObject?,
    private val stepValue: RObject?,
    private val ctx: RapiraParser.ForClauseContext
) : LoopController {
    init {
        // Set initial value using "from" expression
        variable.setValue(fromValue ?: RInteger(1))
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.IDENTIFIER().symbol) }
    }

    override fun isLoopActive(): Boolean {
        if (toValue == null) {
            return true
        }

        return variable.getValue()
            .andThen { obj -> toValue - obj }
            .andThen { obj -> obj * (stepValue ?: RInteger(1)) }
            .andThen { obj -> obj.compare(RInteger(0)) }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.IDENTIFIER().symbol) }
            .let { it >= 0 }
    }

    override fun update() =
        variable.getValue()
            .andThen { obj -> obj + (stepValue ?: RInteger(1)) }
            .andThen { variable.setValue(it) }
            .getOrThrow { reason -> InvalidOperationError(reason, token = ctx.IDENTIFIER().symbol) }
}
