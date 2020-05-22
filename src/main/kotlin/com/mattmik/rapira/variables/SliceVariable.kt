package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.andThen
import com.mattmik.rapira.objects.getOrThrow

class SliceVariable(
    private val baseVariable: Variable,
    private val startIndex: RObject,
    private val endIndex: RObject
) : Variable {
    override var value: RObject
        get() = baseVariable.value.slice(startIndex, endIndex)
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        set(value) {
            if (startIndex !is RInteger || endIndex !is RInteger) {
                throw RapiraInvalidOperationError("Cannot perform slice assignment with non-integer index")
            }

            val leftSlice = baseVariable.value.slice(start = null, end = RInteger(startIndex.value - 1))
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
            val rightSlice = baseVariable.value.slice(start = RInteger(endIndex.value + 1), end = null)
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

            val result = (leftSlice + value)
                .andThen { it + rightSlice }
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

            baseVariable.value = result
        }
}
