package com.mattmik.rapira.objects

const val ESCAPE_SEQUENCE = "\"\""

fun parseEscapedText(escapedText: String) =
    escapedText.substring(1, escapedText.length - 1)
        .replace(ESCAPE_SEQUENCE, "\"")
        .toRapiraText()

/**
 * Returns a formatted string representation of a [RapiraObject] for use with
 * the the `output` statement. This differs from [toString], which returns the
 * system's representation of the object as a string.
 *
 * @param obj the object to format
 */
fun formatRapiraObject(obj: RapiraObject) = when (obj) {
    is RapiraText -> obj.value
    else -> obj.toString()
}

fun Int.toRapiraInteger(): RapiraInteger = RapiraInteger(this)

fun Double.toRapiraReal(): RapiraReal = RapiraReal(this)

fun String.toRapiraText(): RapiraText = RapiraText(this)

fun List<RapiraObject>.toRapiraSequence(): RapiraSequence = RapiraSequence(this)
