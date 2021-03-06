package com.mattmik.rapira

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.nativeFunctions
import com.mattmik.rapira.variables.ReadOnlyVariable
import com.mattmik.rapira.variables.SimpleVariable
import com.mattmik.rapira.variables.Variable

private val specialValues = nativeFunctions + mapOf(
    "empty" to Empty,
    "пусто" to Empty,
    "yes" to CONST_YES,
    "да" to CONST_YES,
    "no" to CONST_NO,
    "нет" to CONST_NO,
    "lf" to CONST_LINE_FEED,
    "пс" to CONST_LINE_FEED,
    "pi" to CONST_PI,
    "пи" to CONST_PI
)

/**
 * A binding of string identifiers to Rapira object values.
 *
 * All Rapira code is executed within an environment. When a procedure or
 * function is called, it is executed in a new environment generated from its
 * parameters, arguments, and parent environment.
 *
 * Environments include bindings for reserved words (for example, `yes` and
 * `no`) as well as user-defined variable assignments.
 */
class Environment private constructor(
    private val bindings: MutableMap<String, Variable>
) {
    constructor() : this(mutableMapOf())

    constructor(environment: Environment) : this(environment.bindings.toMutableMap())

    operator fun set(name: String, variable: Variable) {
        bindings[name] = variable
    }

    operator fun get(name: String): Variable =
        specialValues[name]?.let { ReadOnlyVariable(it) }
            ?: bindings.getOrPut(name, { SimpleVariable(Empty) })

    companion object {
        fun isReserved(name: String) =
            specialValues.containsKey(name)
    }
}
