package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.andThen
import com.mattmik.rapira.util.getOrThrow

class IndexedVariable(
    private val variable: Variable,
    private val index: RObject
) : Variable {

    override var value: RObject
        get() = when (val result = variable.value.elementAt(index)) {
            is Result.Success -> result.obj
            is Result.Error -> throw RapiraInvalidOperationError(result.reason)
        }
        set(value) {
            val leftSliceEnd = (index - RInteger(1))
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
            val rightSliceStart = (index + RInteger(1))
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

            if (variable.value is Text && (value !is Text || value.value.length != 1)) {
                throw RapiraInvalidOperationError("Must pass text of length 1 to index assignment")
            }

            val leftSlice = variable.value.slice(start = null, end = leftSliceEnd)
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
            val rightSlice = variable.value.slice(start = rightSliceStart, end = null)
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
            val middleSlice = if (variable.value is Text) value else listOf(value).toSequence()

            val result = (leftSlice + middleSlice)
                .andThen { it + rightSlice }
                .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

            variable.value = result
        }
}
