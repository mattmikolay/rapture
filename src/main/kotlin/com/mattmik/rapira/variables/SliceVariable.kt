package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject

class SliceVariable(
    private val baseVariable: Variable,
    private val startIndex: RObject,
    private val endIndex: RObject
) : Variable {
    override var value: RObject
        get() = baseVariable.value.slice(startIndex, endIndex)
        set(value) {
            if (startIndex !is RInteger || endIndex !is RInteger) {
                throw RapiraInvalidOperationError("Cannot perform slice assignment with non-integer index")
            }
            baseVariable.value = baseVariable.value.slice(start = null, end = RInteger(startIndex.value - 1)) +
                value +
                baseVariable.value.slice(start = RInteger(endIndex.value + 1), end = null)
        }
}
