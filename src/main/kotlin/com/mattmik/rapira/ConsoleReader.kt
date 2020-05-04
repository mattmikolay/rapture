package com.mattmik.rapira

import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toText

object ConsoleReader {

    fun readText(): Text = (readLine() ?: "").toText()
}
