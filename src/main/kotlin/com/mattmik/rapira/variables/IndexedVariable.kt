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

    override fun getValue() =
        variable.getValue()
            .andThen { it.elementAt(index) }

    override fun setValue(obj: RObject): Result<RObject> {
        val leftSliceEnd = (index - RInteger(1))
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        val rightSliceStart = (index + RInteger(1))
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

        val currentValue = variable.getValue()
        if (currentValue !is Result.Success)
            return currentValue

        if (currentValue.obj is Text && (obj !is Text || obj.value.length != 1)) {
            return Result.Error("Must pass text of length 1 to index assignment")
        }

        val leftSlice = currentValue.obj.slice(start = null, end = leftSliceEnd)
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        val rightSlice = currentValue.obj.slice(start = rightSliceStart, end = null)
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        val middleSlice = if (currentValue.obj is Text) obj else listOf(obj).toSequence()

        val result = (leftSlice + middleSlice)
            .andThen { it + rightSlice }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

        return variable.setValue(result)
    }
}
