package com.mattmik.rapira.objects

const val ESCAPE_SEQUENCE = "\"\""

/**
 * Parses an escaped text string to:
 * 1. Remove any leading or trailing double quotes
 * 2. Convert any escaped double quote pairs (`""`)
 *
 * For example, the string `"""Bother!"""` should yield `"Bother!"`.
 *
 * @see ESCAPE_SEQUENCE
 */
fun parseEscapedText(escapedText: String) =
    escapedText.substring(1, escapedText.length - 1)
        .replace(ESCAPE_SEQUENCE, "\"")
        .toText()

fun Int.toRInteger(): RInteger = RInteger(this)

fun Double.toReal(): Real = Real(this)

fun Boolean.toLogical(): Logical = Logical(this)

fun String.toText(): Text = Text(this)

fun List<RObject>.toSequence(): Sequence = Sequence(this)
