package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.OperationResult
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toSequence

class IndexedVariable(
    private val variable: Variable,
    private val index: RObject
) : Variable {

    override var value: RObject
        get() = when (val result = variable.value.elementAt(index)) {
            is OperationResult.Success -> result.obj
            is OperationResult.Error -> throw RapiraInvalidOperationError(result.reason)
        }
        set(value) {
            val leftSliceEnd = (index.minus(RInteger(1)) as? OperationResult.Success)?.obj
                ?: throw RapiraInvalidOperationError("Invalid index")

            when (variable.value) {
                is Text -> {
                    if (value !is Text || value.value.length != 1) {
                        throw RapiraInvalidOperationError("Must pass text of length 1 to index assignment")
                    }

                    variable.value = variable.value.slice(start = null, end = leftSliceEnd) +
                        value +
                        variable.value.slice(start = index.plus(RInteger(1)), end = null)
                }
                else -> {
                    variable.value = variable.value.slice(start = null, end = leftSliceEnd) +
                        listOf(value).toSequence() +
                        variable.value.slice(start = index.plus(RInteger(1)), end = null)
                }
            }
        }
}
