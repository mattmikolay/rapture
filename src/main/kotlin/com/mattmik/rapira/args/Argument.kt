package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.variables.Variable

interface Argument {
    fun evaluate(environment: Environment): Variable
}
