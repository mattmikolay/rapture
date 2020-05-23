package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.util.Result

interface Variable {
    fun getValue(): Result<RObject>
    fun setValue(obj: RObject): Result<RObject>
}
