package com.mattmik.rapira

import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Text

object ConsoleWriter {

    fun println(message: String) = kotlin.io.println(message)

    fun printObjects(objects: List<RObject>, lineBreak: Boolean) {
        val formattedOutput = objects.joinToString(
            separator = " ",
            postfix = if (lineBreak) System.lineSeparator() else "",
            transform = { obj -> formatObject(obj) }
        )
        print(formattedOutput)
    }

    fun printError(message: String, line: Int? = null, charPositionInLine: Int? = null) =
        if (line != null && charPositionInLine != null)
            System.err.println("Error @ line $line:$charPositionInLine\n\t$message")
        else
            System.err.println("Error\n\t$message")

    /**
     * Returns a formatted string representation of an [RObject] for use with
     * the the `output` statement. This differs from [toString], which returns the
     * system's representation of the object as a string.
     *
     * @param obj the object to format
     */
    fun formatObject(obj: RObject) = when (obj) {
        is Text -> obj.value
        else -> obj.toString()
    }
}
