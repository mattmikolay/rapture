package com.mattmik.rapira.objects

const val ESCAPE_SEQUENCE = "\"\""

fun parseEscapedText(escapedText: String) =
    escapedText.substring(1, escapedText.length - 1)
        .replace(ESCAPE_SEQUENCE, "\"")
        .toText()

/**
 * Returns a formatted string representation of an [RObject] for use with
 * the the `output` statement. This differs from [toString], which returns the
 * system's representation of the object as a string.
 *
 * @param obj the object to format
 */
fun formatRObject(obj: RObject) = when (obj) {
    is Text -> obj.value
    else -> obj.toString()
}

fun Int.toRInteger(): RInteger = RInteger(this)

fun Double.toReal(): Real = Real(this)

fun Boolean.toLogical(): Logical = Logical(this)

fun String.toText(): Text = Text(this)

fun List<RObject>.toSequence(): Sequence = Sequence(this)
