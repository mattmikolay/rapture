package com.mattmik.rapira

import com.mattmik.rapira.objects.RapiraEmpty
import com.mattmik.rapira.objects.RapiraObject

class Environment {
    private val bindings = mutableMapOf<String, RapiraObject>()

    fun setObject(name: String, value: RapiraObject) {
        bindings[name] = value
    }

    fun getObject(name: String) = bindings.getOrDefault(name, RapiraEmpty)
}
