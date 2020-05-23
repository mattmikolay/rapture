package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.shouldError
import com.mattmik.rapira.objects.shouldSucceedWith
import io.kotest.core.spec.style.WordSpec

class ReadOnlyVariableTest : WordSpec({
    "getValue" should {
        "succeed with wrapped object" {
            val obj = Text("Hello, world!")
            ReadOnlyVariable(obj).getValue() shouldSucceedWith obj
        }
    }

    "setValue" should {
        "error" {
            val obj = Text("Hello, world!")
            val variable = ReadOnlyVariable(obj)
            variable.setValue(Text("TEST")).shouldError()
        }
    }
})
