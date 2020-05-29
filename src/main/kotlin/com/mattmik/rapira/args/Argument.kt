package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.variables.Variable
import org.antlr.v4.runtime.Token

/**
 * An argument passed to a function or procedure.
 */
interface Argument {

    val token: Token

    /**
     * Evaluates this argument in a given [environment] and generates a
     * corresponding [Variable].
     */
    fun evaluate(environment: Environment): Variable
}
