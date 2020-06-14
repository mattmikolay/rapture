package com.mattmik.rapira

import com.github.ajalt.clikt.output.defaultCliktConsole
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText
import kotlin.math.PI

val CONST_YES = Logical(true)
val CONST_NO = Logical(false)
val CONST_LINE_FEED = defaultCliktConsole().lineSeparator.toText()
val CONST_PI = PI.toReal()
