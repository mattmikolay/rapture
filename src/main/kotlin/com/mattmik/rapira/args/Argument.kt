package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.variables.Variable

/**
 * An argument passed to a function or procedure.
 */
interface Argument {

    /**
     * Evaluates this argument in a given [environment] and generates a
     * corresponding [Variable].
     */
    fun evaluate(environment: Environment): Variable
}
