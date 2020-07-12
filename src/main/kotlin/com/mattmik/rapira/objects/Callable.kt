package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import org.antlr.v4.runtime.Token

/**
 * A Rapira object value which can be invoked to execute a predefined set of
 * operations. This can be defined in the program source (i.e. functions and
 * procedures), or built into the interpreter itself (i.e. native functions).
 */
interface Callable {

    /**
     * Invokes this callable value.
     *
     * @param environment the environment in which this invocation occurred
     * @param arguments the list of arguments passed to this callable
     * @param callToken the lexical token in the program source at which this
     * invocation took place
     * @return an optional Rapira object value representing the result of the
     * successfully invoked callable
     */
    fun call(
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ): RObject?
}
