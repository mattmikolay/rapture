package com.mattmik.rapira.control

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.OperationResult
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.variables.Variable

class ForLoopController(
    private val variable: Variable,
    fromValue: RObject? = null,
    private val toValue: RObject? = null,
    private val stepValue: RObject? = null
) : LoopController {
    init {
        // Set initial value using "from" expression
        variable.value = fromValue ?: RInteger(1)

        if (toValue != null && toValue !is RInteger && toValue !is Real) {
            throw RapiraInvalidOperationError("To value in for loop must be number")
        }
    }

    override fun isLoopActive(): Boolean {
        if (toValue == null) {
            return true
        }

        val toDifference = ((toValue - variable.value) as? OperationResult.Success)?.obj
            ?: throw RapiraInvalidOperationError("Failed to compute for loop status")

        val forValue = toDifference * (stepValue ?: RInteger(1))
        forValue as? OperationResult.Success
            ?: throw RapiraInvalidOperationError("Failed to compute for loop status")

        return forValue.obj >= RInteger(0)
    }

    override fun update() {
        variable.value = variable.value + (stepValue ?: RInteger(1))
    }
}