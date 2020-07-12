package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.andThen
import com.mattmik.rapira.util.toSuccess

/**
 * A [Variable] that wraps a parent [variable]'s value at a specified [index].
 */
class IndexedVariable(
    private val variable: Variable,
    private val index: Int
) : Variable {

    override fun getValue() =
        variable.getValue()
            .andThen { it.elementAt(index.toRInteger()) }

    override fun setValue(obj: RObject) =
        variable.getValue()
            .andThen {
                when (it) {
                    is Text -> {
                        if (obj !is Text || obj.value.length != 1)
                            Result.Error("Must pass text of length 1 to text index assignment")
                        else
                            obj.toSuccess()
                    }
                    else -> listOf(obj).toSequence().toSuccess()
                }
            }
            .andThen { transformedObject ->
                SliceVariable(
                    baseVariable = variable,
                    startIndex = index.toRInteger(),
                    endIndex = index.toRInteger()
                ).setValue(transformedObject)
            }
}
