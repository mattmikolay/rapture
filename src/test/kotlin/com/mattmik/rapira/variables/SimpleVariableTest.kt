package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.shouldSucceedWith
import io.kotest.core.spec.style.WordSpec

class SimpleVariableTest : WordSpec({
    "getValue" should {
        "succeed with wrapped object" {
            val initialValue = Text("Hello, world!")
            val variable = SimpleVariable(initialValue)
            variable.getValue() shouldSucceedWith initialValue
        }
    }

    "setValue" should {
        "succeed and update variable" {
            val initialValue = Text("Hello, world!")
            val variable = SimpleVariable(initialValue)

            val newValue = Text("TEST")
            variable.setValue(newValue) shouldSucceedWith Unit

            variable.getValue() shouldSucceedWith newValue
        }
    }
})
