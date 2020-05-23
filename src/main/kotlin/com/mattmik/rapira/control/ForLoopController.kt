package com.mattmik.rapira.control

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.util.andThen
import com.mattmik.rapira.util.getOrThrow
import com.mattmik.rapira.variables.Variable

class ForLoopController(
    private val variable: Variable,
    fromValue: RObject? = null,
    private val toValue: RObject? = null,
    private val stepValue: RObject? = null
) : LoopController {
    init {
        // Set initial value using "from" expression
        variable.setValue(fromValue ?: RInteger(1))
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

        if (toValue != null && toValue !is RInteger && toValue !is Real) {
            throw RapiraInvalidOperationError("To value in for loop must be number")
        }
    }

    override fun isLoopActive(): Boolean {
        if (toValue == null) {
            return true
        }

        return variable.getValue()
            .andThen { obj -> toValue - obj }
            .andThen { obj -> obj * (stepValue ?: RInteger(1)) }
            .andThen { obj -> obj.compare(RInteger(0)) }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
            .let { it >= 0 }
    }

    override fun update() {
        variable.getValue()
            .andThen { obj -> obj + (stepValue ?: RInteger(1)) }
            .andThen { variable.setValue(it) }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
    }
}
