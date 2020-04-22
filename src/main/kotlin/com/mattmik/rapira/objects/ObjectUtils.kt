package com.mattmik.rapira.objects

const val ESCAPE_SEQUENCE = "\"\""

fun parseEscapedText(escapedText: String) =
    RapiraText(
        escapedText.substring(1, escapedText.length - 1)
            .replace(ESCAPE_SEQUENCE, "\"")
    )
