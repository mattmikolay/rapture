package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import org.antlr.v4.runtime.Token

interface RCallable {

    fun call(
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ): RObject?
}
