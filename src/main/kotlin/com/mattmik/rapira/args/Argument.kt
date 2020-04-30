package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.objects.RObject

interface Argument {
    fun evaluate(environment: Environment): RObject
}
