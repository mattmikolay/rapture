package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.andThen
import com.mattmik.rapira.util.getOrThrow
import com.mattmik.rapira.util.map

class IndexedVariable(
    private val variable: Variable,
    private val index: Int
) : Variable {

    override fun getValue() =
        variable.getValue()
            .andThen { it.elementAt(index.toRInteger()) }

    override fun setValue(obj: RObject): Result<Unit> {
        val currentValue = variable.getValue()
        if (currentValue !is Result.Success)
            return currentValue.map { Unit }

        if (currentValue.obj is Text && (obj !is Text || obj.value.length != 1)) {
            return Result.Error("Must pass text of length 1 to index assignment")
        }

        val leftSlice = currentValue.obj.slice(end = RInteger(index - 1))
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        val rightSlice = currentValue.obj.slice(start = RInteger(index + 1))
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }
        val middleSlice = if (currentValue.obj is Text) obj else listOf(obj).toSequence()

        val result = (leftSlice + middleSlice)
            .andThen { it + rightSlice }
            .getOrThrow { reason -> RapiraInvalidOperationError(reason) }

        return variable.setValue(result)
    }
}
