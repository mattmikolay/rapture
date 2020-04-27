package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment

interface RapiraCallable {
    fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject?
}
