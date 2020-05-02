package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RObject

class ReadOnlyVariable(private val innerValue: RObject) : Variable {
    override var value: RObject
        get() = innerValue
        set(_) {
            throw RapiraInvalidOperationError("Cannot overwrite reserved word")
        }
}
