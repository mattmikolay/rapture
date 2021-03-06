package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.toSuccess

/**
 * A [Variable] that blocks write access to an instance of [RObject].
 */
class ReadOnlyVariable(private val obj: RObject) : Variable {

    override fun getValue() =
        obj.toSuccess()

    override fun setValue(obj: RObject) =
        Result.Error("Cannot assign to read-only variable")
}
