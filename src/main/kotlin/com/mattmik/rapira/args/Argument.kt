package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.objects.RapiraObject

interface Argument {
    fun evaluate(environment: Environment): RapiraObject
}
