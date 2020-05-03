package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.toSequence

class IndexedVariable(
    private val variable: Variable,
    private val index: RObject
) : Variable {

    override var value: RObject
        get() = variable.value.elementAt(index)
        set(value) {
            // TODO Look at quirks of index assignment, p24
            variable.value = variable.value.slice(start = null, end = index.minus(RInteger(1))) +
                listOf(value).toSequence() +
                variable.value.slice(start = index.plus(RInteger(1)), end = null)
        }
}
