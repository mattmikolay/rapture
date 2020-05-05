package com.mattmik.rapira.objects

enum class ParamType {
    In,
    InOut,
}

data class Parameter(val type: ParamType, val name: String)
