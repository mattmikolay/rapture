package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RapiraEmpty
import com.mattmik.rapira.objects.RapiraLogical
import com.mattmik.rapira.objects.RapiraObject

private val specialValues = mapOf(
    "empty" to RapiraEmpty,
    "yes" to RapiraLogical(true),
    "no" to RapiraLogical(false)
)

class Environment {
    private val bindings = mutableMapOf<String, RapiraObject>()

    operator fun set(name: String, value: RapiraObject) {
        if (specialValues.containsKey(name)) {
            throw RapiraInvalidOperationError("Cannot overwrite reserved word $name")
        }
        bindings[name] = value
    }

    operator fun get(name: String) = specialValues[name] ?: bindings.getOrDefault(name, RapiraEmpty)
}
