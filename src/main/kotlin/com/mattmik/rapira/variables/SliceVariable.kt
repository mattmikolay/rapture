package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.andThen

/**
 * A [Variable] that wraps the values in [baseVariable] between two indexes:
 * [startIndex] and [endIndex].
 */
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

        return baseVariable.getValue()
            .andThen {
                Result.zip(
                    it.slice(end = RInteger(startIndex.value - 1)),
                    it.slice(start = RInteger(endIndex.value + 1))
                )
            }
            .andThen { (leftSlice, rightSlice) -> mergeObjects(leftSlice, obj, rightSlice) }
            .andThen { newValue -> baseVariable.setValue(newValue) }
    }
}

private fun mergeObjects(obj1: RObject, obj2: RObject, obj3: RObject) =
    (obj1 + obj2).andThen { it + obj3 }
