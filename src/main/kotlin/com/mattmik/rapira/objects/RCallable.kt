package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument

interface RCallable {
    fun call(environment: Environment, arguments: List<Argument>): RObject?
}
