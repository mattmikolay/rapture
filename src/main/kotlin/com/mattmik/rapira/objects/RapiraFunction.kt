package com.mattmik.rapira.objects

import com.mattmik.rapira.antlr.RapiraLangParser

// TODO: Add params and intern/extern declarations
class RapiraFunction(val bodyStatements: RapiraLangParser.StmtsContext? = null) : RapiraObject("function") {
    override fun toString() = "function"
}
