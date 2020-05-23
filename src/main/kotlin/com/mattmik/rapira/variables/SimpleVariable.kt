package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.util.toSuccess

class SimpleVariable(private var value: RObject) : Variable {

    override fun getValue(): Result<RObject> =
        value.toSuccess()

    override fun setValue(obj: RObject): Result<Unit> {
        value = obj
        return Result.Success(Unit)
    }
}
