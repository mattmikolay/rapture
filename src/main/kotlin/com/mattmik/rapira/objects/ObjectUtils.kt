package com.mattmik.rapira.objects

const val ESCAPE_SEQUENCE = "\"\""

fun parseEscapedText(escapedText: String) =
    escapedText.substring(1, escapedText.length - 1)
        .replace(ESCAPE_SEQUENCE, "\"")
        .toRText()

/**
 * Returns a formatted string representation of an [RObject] for use with
 * the the `output` statement. This differs from [toString], which returns the
 * system's representation of the object as a string.
 *
 * @param obj the object to format
 */
fun formatRObject(obj: RObject) = when (obj) {
    is RText -> obj.value
    else -> obj.toString()
}

fun Int.toRInteger(): RInteger = RInteger(this)

fun Double.toRReal(): RReal = RReal(this)

fun String.toRText(): RText = RText(this)

fun List<RObject>.toRSequence(): RSequence = RSequence(this)
