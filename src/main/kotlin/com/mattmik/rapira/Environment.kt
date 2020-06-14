package com.mattmik.rapira

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.LogicalNo
import com.mattmik.rapira.objects.LogicalYes
import com.mattmik.rapira.objects.nativeFunctions
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText
import com.mattmik.rapira.variables.ReadOnlyVariable
import com.mattmik.rapira.variables.SimpleVariable
import com.mattmik.rapira.variables.Variable
import kotlin.math.PI

private val specialValues = nativeFunctions + mapOf(
    "empty" to Empty,
    "пусто" to Empty,
    "yes" to LogicalYes,
    "да" to LogicalYes,
    "no" to LogicalNo,
    "нет" to LogicalNo,
    "lf" to System.lineSeparator().toText(),
    "пс" to System.lineSeparator().toText(),
    "pi" to PI.toReal(),
    "пи" to PI.toReal()
)

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
