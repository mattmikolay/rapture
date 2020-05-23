package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.andThen
import com.mattmik.rapira.util.getOrThrow
import com.mattmik.rapira.util.map

class SliceVariable(
    private val baseVariable: Variable,
    private val startIndex: RObject,
    private val endIndex: RObject
) : Variable {

    override fun getValue() =
        baseVariable.getValue()
            .andThen { it.slice(startIndex, endIndex) }

    override fun setValue(obj: RObject): Result<Unit> {
        if (startIndex !is RInteger || endIndex !is RInteger) {
            return Result.Error("Cannot perform slice assignment with non-integer index")
        }

        val currentValue = baseVariable.getValue()
        if (currentValue !is Result.Success)
            return currentValue.map { Unit }

        val leftSlice = currentValue.obj.slice(end = RInteger(startIndex.value - 1))
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        val rightSlice = currentValue.obj.slice(start = RInteger(endIndex.value + 1))
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

        val result = (leftSlice + obj)
            .andThen { it + rightSlice }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

        return baseVariable.setValue(result)
    }
}
